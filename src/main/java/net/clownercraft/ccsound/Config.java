package net.clownercraft.ccsound;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    ccSoundScape plugin;

    public Config(ccSoundScape plugin) {
        this.plugin = plugin;

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

        if (conf.getInt("confVersion",0)<confVersion) {
            //Update old config files with new settings
            save();
        }
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

        try {
            conf.save(new File(plugin.getDataFolder(),"config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        //TODO
    }

    public String getMessage(String key) {
        String out = "";
        switch (key) {
            case "musicToggleOn": out = commandPrefix + musicToggleOn; break;
            case "musicToggleOff": out = commandPrefix + musicToggleOff; break;
            case "commandPlayerOnly": out = commandPrefix + commandPlayerOnly; break;
        }
        return formatMessage(out);
    }

    public String formatMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&',msg);
    }
}
