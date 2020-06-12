package net.clownercraft.ccsound;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    public HashMap<String,Location> SONGS = new HashMap(); //Midi filename, Location


    public Config() {
        YamlConfiguration conf;
        File configFile = new File(ccSoundScape.getInstance().getDataFolder(),"config.yml");
        if (!configFile.exists()) ccSoundScape.getInstance().saveDefaultConfig();

        conf = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection sec = conf.getConfigurationSection("songs");

        Set<String> keys = sec.getKeys(false);

        Iterator keyIterator = keys.iterator();

        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            Location loc = (Location) sec.get(key+".loc");
            SONGS.put(key.replace('_','.'),loc);
        }
    }


    public void save() {
        FileConfiguration conf = new YamlConfiguration();

        conf.createSection("songs");

        Set<String> keys = SONGS.keySet();
        Iterator keyIterator = keys.iterator();

        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            Location loc = SONGS.get(key);
            key = key.replace('.','_');

            conf.createSection("songs."+key+".loc");
            conf.set("songs."+key+".loc",loc);
        }

        try {
            conf.save(new File(ccSoundScape.getInstance().getDataFolder(),"config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
