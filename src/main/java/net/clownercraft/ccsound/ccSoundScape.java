package net.clownercraft.ccsound;

import lombok.Getter;
import net.clownercraft.ccsound.command.SoundCommand;
import net.clownercraft.ccsound.command.ToggleCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.midiplayer.MidiPlayerMain;
import org.primesoft.midiplayer.MusicPlayer;
import org.primesoft.midiplayer.track.LocationTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Getter
public class ccSoundScape extends JavaPlugin {
    private Config conf;
    private MusicPlayer player;
    @Deprecated private final HashMap<String,ArrayList<LocationTrack>> tracks = new HashMap<>();//String = track file, LocationTrack = track
    private final HashMap<String,ArrayList<Player>> trackPlayers = new HashMap<>();
    private final ArrayList<Player> disabledPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        //Load config
        conf = new Config(this);
        this.getLogger().info("Loaded Config");

        //register commands
        Objects.requireNonNull(this.getCommand("ccsound")).setExecutor(new SoundCommand(this));
        Objects.requireNonNull(this.getCommand("togglemusic")).setExecutor(new ToggleCommand(this));

        //Start music
        player = MidiPlayerMain.getInstance().getMusicPlayer();
        try{
            startPlayers();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updatePlayers,conf.distanceCheckDelay,conf.distanceCheckDelay);
    }

    @Override
    public void onDisable() {
        stopPlayers();
    }

    public void stopPlayers() {
        for (TrackPlayer s : conf.getLocations().values()) {
            s.stopPlayer();
        }
    }

    public void startPlayers() {
        this.getLogger().info("Starting Music Players.");
        for (TrackPlayer s : conf.getLocations().values()) {
            s.startPlaying();
        }
    }

    public void updatePlayers() {
        for (TrackPlayer trackPlayer:conf.getLocations().values()) trackPlayer.checkPlayersInRange();
    }

    //Toggle the music on/off for player
    public void togglePlayer(Player player) {
        if (disabledPlayers.contains(player)) {
            disabledPlayers.remove(player);
            player.sendMessage(this.getConf().getMessage("musicToggleOn"));
        } else {
            disabledPlayers.add(player);
            player.sendMessage(this.getConf().getMessage("musicToggleOff"));
        }
    }

    public void reload() {
        stopPlayers();
        this.getConf().loadSettings();
        startPlayers();
    }
}
