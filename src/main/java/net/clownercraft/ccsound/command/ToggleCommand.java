package net.clownercraft.ccsound.command;

import lombok.AllArgsConstructor;
import net.clownercraft.ccsound.ccSoundScape;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ToggleCommand implements CommandExecutor {
    ccSoundScape plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getConf().getMessage("commandPlayerOnly"));
            return true;
        }

        plugin.togglePlayer((Player) commandSender);
        return true;
    }
}
