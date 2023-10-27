package net.clownercraft.ccsound;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class Config {
    final HashMap<String,TrackPlayer> locations = new HashMap<>();
    static final int confVersion = 2;

    int playerRangeX = 40, playerRangeY = 40, playerRangeZ = 40;
    int distanceCheckDelay = 60;

    String musicToggleOn = "&5[Music turned back on.]";
    String musicToggleOff = "&5[Music turned off.]";
    String commandPrefix = "&b&lccSoundscape &b» ";
    String commandPlayerOnly = "&cThis command can only be used by players.";

    String commandNoPerms = "&cYou do not have permission for that.";
    String commandReload = "&9Plugin reloaded";
    String commandNotEnoughArgs = "&cNot enough arguments - see &7/ccsound help";
    String commandNameTaken = "&cA location by that name already exists!";
    String commandLocationAdded = "&9Added location";
    String commandLocationNotExist = "&cThat location does not exist";
    String commandLocationRemoved = "&9Location removed";
    String commandTrackAdded = "&9Track added";
    String commandTrackNotExist = "&cThat location has no track by that name";
    String commandTrackRemoved = "&9Track removed";
    String commandHelp = "&b&lccSoundScape &b» &9&lHelp:\n" +
            "&7/ccsound reload &9Reload the plugin\n" +
            "&7/ccsound addLocation &8<&7name&8> &9Add a new music player at your current location\n" +
            "&7/ccsound removeLocation &8<&7name&8> &9Remove a music player\n" +
            "&7/ccsound addTrack &8<&7LocationName&8> &8<&7MidiFileName&8>\n" +
            "&7/ccsound removeTrack  &8<&7LocationName&8> &8<&7MidiFileName&8>\n" +
            "&9  Add/remove a track from a location. File name should include .mid\n";

    ccSoundScape plugin;

    public Config(ccSoundScape plugin) {
        this.plugin = plugin;
        loadSettings();
    }

    public void save() {
        FileConfiguration conf = new YamlConfiguration();

        conf.set("confVersion",confVersion);

        //Settings
        conf.set("Settings.PlayerRange.X",playerRangeX);
        conf.set("Settings.PlayerRange.Y",playerRangeY);
        conf.set("Settings.PlayerRange.Z",playerRangeZ);
        conf.set("Settings.DistanceCheckDelay",distanceCheckDelay);

        //Playlists
        ConfigurationSection section = conf.createSection("Playlists");
        for (String key : locations.keySet()) {
            section.set(key,locations.get(key).getConfig());
        }

        //messages section
        conf.set("Messages.musicToggleOn",musicToggleOn);
        conf.set("Messages.musicToggleOff",musicToggleOff);
        conf.set("Messages.commandPrefix",commandPrefix);
        conf.set("Messages.commandPlayerOnly",commandPlayerOnly);
        conf.set("Messages.commandNoPerms",commandNoPerms);
        conf.set("Messages.commandReload",commandReload);
        conf.set("Messages.commandNotEnoughArgs",commandNotEnoughArgs);
        conf.set("Messages.commandNameTaken",commandNameTaken);
        conf.set("Messages.commandLocationAdded",commandLocationAdded);
        conf.set("Messages.commandLocationNotExist",commandLocationNotExist);
        conf.set("Messages.commandLocationRemoved",commandLocationRemoved);
        conf.set("Messages.commandTrackAdded",commandTrackAdded);
        conf.set("Messages.commandTrackNotExist",commandTrackNotExist);
        conf.set("Messages.commandTrackRemoved",commandTrackRemoved);
        conf.set("Messages.commandHelp",commandHelp);


        try {
            conf.save(new File(plugin.getDataFolder(),"config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        YamlConfiguration conf;
        File configFile = new File(plugin.getDataFolder(),"config.yml");
        if (!configFile.exists()) plugin.saveDefaultConfig();

        conf = YamlConfiguration.loadConfiguration(configFile);

        //Settings
        playerRangeX = conf.getInt("Settings.PlayerRange.X",playerRangeX);
        playerRangeY = conf.getInt("Settings.PlayerRange.Y",playerRangeY);
        playerRangeZ = conf.getInt("Settings.PlayerRange.Z",playerRangeZ);
        distanceCheckDelay = conf.getInt("Settings.DistanceCheckDelay",distanceCheckDelay);

        //Playlists
        ConfigurationSection section = conf.getConfigurationSection("Playlists");
        if (section!=null) {
            for (String key:section.getKeys(false)) {
                TrackPlayer trackPlayer = new TrackPlayer(plugin,key,Objects.requireNonNull(section.getConfigurationSection(key)));
                locations.put(key,trackPlayer);
            }
        }

        //messages section
        musicToggleOn = conf.getString("Messages.musicToggleOn",musicToggleOn);
        musicToggleOff = conf.getString("Messages.musicToggleOff",musicToggleOff);
        commandPrefix = conf.getString("Messages.commandPrefix",commandPrefix);
        commandPlayerOnly = conf.getString("Messages.commandPlayerOnly",commandPlayerOnly);
        commandNoPerms = conf.getString("Messages.commandNoPerms",commandNoPerms);
        commandReload = conf.getString("Messages.commandReload",commandReload);
        commandNotEnoughArgs = conf.getString("Messages.commandNotEnoughArgs",commandNotEnoughArgs);
        commandNameTaken = conf.getString("Messages.commandNameTaken",commandNameTaken);
        commandLocationAdded = conf.getString("Messages.commandLocationAdded",commandLocationAdded);
        commandLocationNotExist = conf.getString("Messages.commandLocationNotExist",commandLocationNotExist);
        commandLocationRemoved = conf.getString("Messages.commandLocationRemoved",commandLocationRemoved);
        commandTrackAdded = conf.getString("Messages.commandTrackAdded",commandTrackAdded);
        commandTrackNotExist = conf.getString("Messages.commandTrackNotExist",commandTrackNotExist);
        commandTrackRemoved = conf.getString("Messages.commandTrackRemoved",commandTrackRemoved);
        commandHelp = conf.getString("Messages.commandHelp",commandHelp);

        if (conf.getInt("confVersion",0)<confVersion) {
            //Update old config files with new settings
            save();
        }
    }

    public String getMessage(String key) {
        String out = "";
        switch (key) {
            case "musicToggleOn": out = commandPrefix + musicToggleOn; break;
            case "musicToggleOff": out = commandPrefix + musicToggleOff; break;
            case "commandPlayerOnly": out = commandPrefix + commandPlayerOnly; break;
            case "commandNoPerms": out = commandPrefix + commandNoPerms; break;
            case "commandReload": out = commandPrefix + commandReload; break;
            case "commandNotEnoughArgs": out = commandPrefix + commandNotEnoughArgs; break;
            case "commandNameTaken": out = commandPrefix + commandNameTaken; break;
            case "commandLocationAdded": out = commandPrefix + commandLocationAdded; break;
            case "commandLocationNotExist": out = commandPrefix + commandLocationNotExist; break;
            case "commandLocationRemoved": out = commandPrefix + commandLocationRemoved; break;
            case "commandTrackAdded": out = commandPrefix + commandTrackAdded; break;
            case "commandTrackNotExist": out = commandPrefix + commandTrackNotExist; break;
            case "commandTrackRemoved": out = commandPrefix + commandTrackRemoved; break;
            case "commandHelp": out = commandHelp; break;
        }
        return formatMessage(out);
    }

    public String formatMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&',msg);
    }
}
