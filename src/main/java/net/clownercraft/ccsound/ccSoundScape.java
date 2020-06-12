package net.clownercraft.ccsound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.midiplayer.MidiPlayerMain;
import org.primesoft.midiplayer.MusicPlayer;
import org.primesoft.midiplayer.midiparser.MidiParser;
import org.primesoft.midiplayer.midiparser.NoteTrack;
import org.primesoft.midiplayer.track.LocationTrack;

import java.io.File;
import java.util.*;

/**
 * Created by new on 21/01/2019.
 */
public class ccSoundScape extends JavaPlugin {
    private static ccSoundScape instance;
    private static Config conf;
    private MusicPlayer player;
    private HashMap<String,LocationTrack> tracks = new HashMap();//String = track file, LocationTrack = track
    private HashMap<String,ArrayList<Player>> trackPlayers = new HashMap();
    private ArrayList<Player> disabledPlayers = new ArrayList();

    public static ccSoundScape getInstance() {
        return instance;
    }
    public static Config getConf() {
        return conf;
    }


    @Override
    public void onEnable() {
        instance = this;
        //Load config
        conf = new Config();
        this.getLogger().info("Loaded Config");

        //register commands
        getCommand("ccsound").setExecutor(new commandListener());
        getCommand("togglemusic").setExecutor(new commandListener());

        //Start music
        player = MidiPlayerMain.getInstance().getMusicPlayer();
        try{
            loadTracks();
            startPlayers();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                updatePlayers();
            }
        },20,20);
    }

    @Override
    public void onDisable() {
        stopPlayers();
    }

    public void resetPlayers() {
        stopPlayers();
        loadTracks();
        startPlayers();
    }

    public void stopPlayers() {
        Iterator trackIterator = tracks.keySet().iterator();
        while (trackIterator.hasNext()) {
            LocationTrack track = tracks.get(trackIterator.next());
            player.removeTrack(track);
        }

    }

    public void startPlayers() {
        this.getLogger().info("Starting Music Players.");
        Iterator trackIterator = tracks.keySet().iterator();
        while (trackIterator.hasNext()) {
            LocationTrack track = tracks.get(trackIterator.next());
            player.playTrack(track);
        }
    }

    public void loadTracks() {
        //remove old trakcs
        tracks.clear();

        //get track names from SONGS
        Set<String> keys = conf.SONGS.keySet();
        Iterator keyIterator = keys.iterator();


        while (keyIterator.hasNext()) {

            String trackname = (String) keyIterator.next();
            this.getLogger().info("Loading track " + trackname );
            //Load midifile
            File midifile = new File(MidiPlayerMain.getInstance().getDataFolder(),trackname);
            NoteTrack track = MidiParser.loadFile(midifile);

            if (track.isError()) {
                getLogger().warning(track.getMessage());
            }

            //get Location & Add track
            Location loc = conf.SONGS.get(trackname);
            tracks.put(trackname, new LocationTrack(loc, track.getNotes(), true));
            trackPlayers.put(trackname,new ArrayList<Player>());
            this.getLogger().info("Loaded");
        }
    }



    public void updatePlayers() {
        Iterator trackIterator = tracks.keySet().iterator();

        while (trackIterator.hasNext()) {
            String trackname = (String) trackIterator.next();

            Location loc = conf.SONGS.get(trackname);

            World world = loc.getWorld();

            Collection<Entity> entities = world.getNearbyEntities(loc,40,40,40);
            ArrayList<Player> players = new ArrayList();

            for (Entity e : entities){
                if (e instanceof Player && !disabledPlayers.contains(e)){
                    players.add((Player) e);
                }
            }
            //Check existing players still within area
            ArrayList<Player> trackPlayerList = trackPlayers.get(trackname);

            try{
                Iterator oldPlayerIterator = trackPlayerList.iterator();
            while (oldPlayerIterator.hasNext()) {
                Player current = (Player) oldPlayerIterator.next();
                if (!players.contains(current)) {
                    trackPlayerList.remove(current);
                    tracks.get(trackname).removePlayer(current);
                }
            }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //add extra players
            try {
                Iterator playerInterator = players.iterator();

                while (playerInterator.hasNext()) {
                    Player next = (Player) playerInterator.next();

                    if (!trackPlayerList.contains(next)) {
                        trackPlayerList.add(next);
                        tracks.get(trackname).addPlayer(next);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    //Toggle the music on/off for player
    public void togglePlayer(Player player) {
        if (disabledPlayers.contains(player)) {
            disabledPlayers.remove(player);
            player.sendMessage(ChatColor.DARK_PURPLE + "[Music turned back on.]");
        } else {
            disabledPlayers.add(player);
            player.sendMessage(ChatColor.DARK_PURPLE + "[Music turned off.]");
        }
    }
}
