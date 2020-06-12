package net.clownercraft.ccsound;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class commandListener implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getLabel().equals("ccsound")) {
            if (sender.hasPermission("ccsoundscape.admin")) {
                if (args.length < 2) {

                    return false;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    //Add new track
                    String track = args[1];
                    Location loc = ((Player) sender).getLocation();

                    ccSoundScape.getConf().SONGS.put(track, loc);
                    ccSoundScape.getConf().save();
                    ccSoundScape.getInstance().resetPlayers();
                    return true;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    //remove a track
                    String track = args[1];

                    if (!ccSoundScape.getConf().SONGS.containsKey(track)) {
                        return false;
                    }

                    ccSoundScape.getConf().SONGS.remove(track);
                    ccSoundScape.getConf().save();
                    ccSoundScape.getInstance().resetPlayers();
                    return true;
                }
            }
            return false;
        } else if (command.getLabel().equals("togglemusic")) {
            ccSoundScape.getInstance().togglePlayer((Player) sender);
            return true;
        } else return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> out = new ArrayList();
        if (s.equalsIgnoreCase("ccsound")) {
            if (strings.length==1) {
                out.add("add");
                out.add("remove");
            }
        }
        return out;
    }
}