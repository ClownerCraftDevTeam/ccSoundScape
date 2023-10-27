package net.clownercraft.ccsound.command;

import lombok.AllArgsConstructor;
import net.clownercraft.ccsound.TrackPlayer;
import net.clownercraft.ccsound.ccSoundScape;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ccsoundscape.admin")) {
            commandSender.sendMessage(plugin.getConf().getMessage("commandNoPerms")); //TODO make this
            return true;
        }
        if (args.length==0) args = new String[]{"help"};
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                commandSender.sendMessage(plugin.getConf().getMessage("commandReload")); //TODO make this
                return true;
            case "addlocation": {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandPlayerOnly"));
                    return true;
                }

                if (args.length<2) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs")); //TODO make this
                    return true;
                }

                String name = args[1];
                if (plugin.getConf().getLocations().containsKey(name)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNameTaken")); //TODO make this
                    return true;
                }

                Location l = ((Player) commandSender).getLocation();
                TrackPlayer p = new TrackPlayer(plugin,l,name);
                plugin.getConf().getLocations().put(name,p);
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandLocationAdded")); //TODO make this
                return true;
            }
            case "removelocation": {
                if (args.length<2) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandNotEnoughArgs")); //TODO make this
                    return true;
                }

                String name = args[1];
                if (!plugin.getConf().getLocations().containsKey(name)) {
                    commandSender.sendMessage(plugin.getConf().getMessage("commandLocationNotExist")); //TODO make this
                    return true;
                }

                TrackPlayer player = plugin.getConf().getLocations().remove(name);
                player.stopPlayer();
                plugin.getConf().save();
                commandSender.sendMessage(plugin.getConf().getMessage("commandLocationRemoved")); //TODO make this
                return true;
            }
            case "addtrack": {
                //TODO
                return true;
            }
            case "removetrack": {
                //TODO
                return true;
            }
            default:
                commandSender.sendMessage(plugin.getConf().getMessage("commandHelp")); //TODO make this
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        List<String> out = new ArrayList<>();
        //TODO
        return out;
    }
}