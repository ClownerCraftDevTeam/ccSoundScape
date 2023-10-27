package net.clownercraft.ccsound;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.primesoft.midiplayer.MidiPlayerMain;
import org.primesoft.midiplayer.midiparser.MidiParser;
import org.primesoft.midiplayer.midiparser.NoteFrame;
import org.primesoft.midiplayer.midiparser.NoteTrack;
import org.primesoft.midiplayer.track.LocationTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

@Getter
public class TrackPlayer {
    ccSoundScape plugin;
    String name;
    ArrayList<String> trackNames; //Track file names as per config
    HashMap<String,NoteTrack> notes; //Loaded midi
    HashMap<String,LocationTrack> tracks; //Loaded midi
    @Setter Location location;

    //Functional Data
    @Setter int currentTrack = 0;
    ArrayList<Player> playersInRange = new ArrayList<>();
    BukkitTask nextTrackStarter = null;

    public TrackPlayer(ccSoundScape plugin, Location l, String name) {
        this.plugin = plugin;
        this.location = l;
        this.name = name;
        this.trackNames = new ArrayList<>();
        this.notes = new HashMap<>();
        this.tracks = new HashMap<>();
    }

    public TrackPlayer(ccSoundScape plugin, String name, ConfigurationSection config) {
        this.plugin = plugin;
        this.name = name;
        this.location = config.getLocation("location");
        this.trackNames = (ArrayList<String>) config.getStringList("tracks");
        this.notes = new HashMap<>();
        this.tracks = new HashMap<>();
        reloadTracks();
    }

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new YamlConfiguration();
        config.set("location",location);
        config.set("tracks",trackNames);
        return config;
    }

    public void addTrack(String name) {
        trackNames.add(name);
        reloadTracks();
    }

    public void removeTrack(String name) {
        trackNames.remove(name);
        reloadTracks();
    }

    public void reloadTracks() {
        tracks.clear();
        for (String trackname : trackNames) {
            //Load midi files
            File midifile = new File(MidiPlayerMain.getInstance().getDataFolder(), trackname);
            NoteTrack track = MidiParser.loadFile(midifile);

            if (track.isError()) {
                plugin.getLogger().warning("Error Loading track " + trackname + "!");
                plugin.getLogger().warning(track.getMessage());
                continue;
            }

            LocationTrack locationTrack = new LocationTrack(location,track.getNotes(),false);

            notes.put(trackname,track);
            tracks.put(trackname,locationTrack);
            plugin.getLogger().info("Loaded track " + trackname);
        }
    }

    public void checkPlayersInRange() {
        if (location == null) return;
        World world = location.getWorld();
        if (world==null) return;

        Collection<Entity> entities = world.getNearbyEntities(
                location,
                plugin.getConf().playerRangeX,
                plugin.getConf().playerRangeY,
                plugin.getConf().playerRangeZ);

        ArrayList<Player> newPlayers = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof Player && !plugin.getDisabledPlayers().contains(e)) {
                newPlayers.add((Player) e);
            }
        }

        //remove players that are now not in range
        Iterator<Player> iterator = playersInRange.iterator();
        while (iterator.hasNext()) {
            Player current = iterator.next();
            if (!newPlayers.contains(current)) {
                iterator.remove();
                for (LocationTrack track:tracks.values()) track.removePlayer(current);
            }
        }

        //add extra players
        for (Player next : newPlayers) {
            if (!playersInRange.contains(next)) {
                playersInRange.add(next);
                for (LocationTrack track:tracks.values()) track.addPlayer(next);
            }
        }
    }

    public void startPlaying() {
        if (location==null) return;

        currentTrack = 0;
        if (!playTrack(currentTrack)) {
            currentTrack++;
            if (currentTrack>=trackNames.size()) {
                plugin.getLogger().warning("All tracks may be invalid for player: " + name);
                return;
            }
            startPlaying();
        }
    }

    public void stopPlayer() {
        if (tracks.size()==0) return;
        plugin.getPlayer().removeTrack(tracks.get(trackNames.get(currentTrack)));
        if (nextTrackStarter!=null && !nextTrackStarter.isCancelled()) nextTrackStarter.cancel();
    }

    public boolean playTrack(int id) {
        LocationTrack track = tracks.getOrDefault(trackNames.get(id),null);
        if (track==null) return false;
        plugin.getPlayer().playTrack(track);
        NoteTrack notes = this.notes.get(trackNames.get(id));
        long tracklength = calcTrackLength(notes) / 50; //In ticks
        nextTrackStarter = Bukkit.getScheduler().runTaskLater(plugin,this::startNextTrack,tracklength);
        return true;
    }

    public void startNextTrack() {
        plugin.getPlayer().removeTrack(tracks.get(trackNames.get(currentTrack)));

        currentTrack++; if (currentTrack>=trackNames.size()) currentTrack = 0;
        if (!playTrack(currentTrack)) {
            //Skip invalid tracks
            currentTrack++; if (currentTrack>=trackNames.size()) currentTrack = 0;
            playTrack(currentTrack);
        }
    }

    /**
     * Check how long before the next track should be started
     * @param track the track to calculate
     * @return number of milliseconds of the total song
     */
    public long calcTrackLength(NoteTrack track) {
        long out = 0;
        for (NoteFrame note: track.getNotes()) {
            out+=note.getWait();
        }
        return out;
    }
}
