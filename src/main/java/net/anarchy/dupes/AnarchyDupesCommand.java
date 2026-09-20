package net.anarchy.dupes.command;

import net.anarchy.dupes.gui.DupeConfigGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AnarchyDupesCommand implements CommandExecutor {

    private final DupeConfigGUI gui;

    public AnarchyDupesCommand(Plugin plugin) {
        this.gui = new DupeConfigGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        if (!player.hasPermission("anarchydupes.admin")) {
            player.sendMessage("§cYou do not have permission to access the config menu!");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("config")) {
            gui.openGUI(player);
            return true;
        }

        player.sendMessage("§cUsage: /anarchydupes config");
        return true;
    }
}