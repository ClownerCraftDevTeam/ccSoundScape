package net.clownercraft.ccsound.command;

import lombok.AllArgsConstructor;
import net.clownercraft.ccsound.TrackPlayer;
import net.clownercraft.ccsound.ccSoundScape;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("NullableProblems")
@AllArgsConstructor
public class SoundCommand implements CommandExecutor, TabCompleter {
    ccSoundScape plugin;

    /**
     * Handle the command
     * /ccsound addLocation [name]
     * /ccsound removelocation [name]
     * /ccsound addtrack [location] [track]
     * /ccsound removetrack [location] [track]
     * /ccsound reload
     *
     * @param commandSender - the command sender
     * @param command - the command
     * @param label - the command name
     * @param args - the arguments
     * @return true if successful
     */
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ccsoundscape.admin")) {
            commandSender.sendMessage(plugin.getConf().getMessage("commandNoPerms"));
            return true;
        }
        if (args.length==0) args = new String[]{"help"};
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                commandSender.sendMessage(plugin.getConf().getMessage("commandReload"));
                return true;
            case "addlocation": {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandPlayerOnly"));
                    return true;
                }

                if (args.length<2) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs"));
                    return true;
                }

                String name = args[1];
                if (plugin.getConf().getLocations().containsKey(name)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNameTaken"));
                    return true;
                }

                Location l = ((Player) commandSender).getLocation();
                TrackPlayer p = new TrackPlayer(plugin,l,name);
                plugin.getConf().getLocations().put(name,p);
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandLocationAdded"));
                return true;
            }
            case "removelocation": {
                if (args.length<2) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs"));
                    return true;
                }

                String name = args[1];
                if (!plugin.getConf().getLocations().containsKey(name)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandLocationNotExist"));
                    return true;
                }

                TrackPlayer player = plugin.getConf().getLocations().remove(name);
                player.stopPlayer();
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandLocationRemoved"));
                return true;
            }
            case "addtrack": {
                if (args.length<3) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs"));
                    return true;
                }
                String location = args[1];
                if (!plugin.getConf().getLocations().containsKey(location)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandLocationNotExist"));
                    return true;
                }
                TrackPlayer player = plugin.getConf().getLocations().get(location);
                String trackName = args[2];
                player.addTrack(trackName);
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandTrackAdded"));
                return true;
            }
            case "removetrack": {
                if (args.length<3) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs"));
                    return true;
                }
                String location = args[1];
                if (!plugin.getConf().getLocations().containsKey(location)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandLocationNotExist"));
                    return true;
                }
                TrackPlayer player = plugin.getConf().getLocations().get(location);
                String trackName = args[2];
                if (!player.getTrackNames().contains(trackName)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandTrackNotExist"));
                    return true;
                }
                player.removeTrack(trackName);
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandTrackRemoved"));
                return true;
            }
            default:
                commandSender.sendMessage(plugin.getConf().getMessage("commandHelp"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length==1) {
            out = Arrays.asList("help","reload","addLocation","removeLocation","addTrack","removeTrack");
        }
        if (args.length==2) {
            String sub = args[0];
            switch (sub.toLowerCase()) {
                case "removelocation":
                case "addtrack":
                case "removetrack":
                    out = Arrays.asList((plugin.getConf().getLocations().keySet().toArray(new String[0])));
                    break;
                default:
                    out = Collections.singletonList("");
                    break;
            }
        }
        if (args.length==3) {
            String sub = args[0];
            if (!sub.equalsIgnoreCase("removeTrack")) {
                out = Collections.singletonList("");
            } else {
                String location = args[1];
                TrackPlayer player = plugin.getConf().getLocations().getOrDefault(location,null);
                if (player!=null) {
                    out = player.getTrackNames();
                } else out = Collections.singletonList("");
            }
        }
        return out;
    }
}