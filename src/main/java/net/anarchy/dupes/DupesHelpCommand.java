package net.anarchy.dupes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class DupesHelpCommand implements CommandExecutor {

    private final Plugin plugin;

    public DupesHelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        sender.sendMessage("§8§m----------------§r §6§lANARCHY DUPES §8§m----------------");
        sender.sendMessage("§7Active dupe methods & instructions on this server:");
        sender.sendMessage("");

        // 1. Item Frame Dupe
        sendDupeInfo(sender, config, "item_frame", "Item Frame Dupe",
                "Place an item into an Item Frame, then §eLeft-Click (Punch) §fto break it out.");

        // 2. Cactus Dupe
        sendDupeInfo(sender, config, "cactus", "Cactus Dupe",
                "Drop an item manually using §eQ §ffor §eCtrl+Q §fdirectly onto a Cactus block.");

        // 3. Dispenser Dupe
        sendDupeInfo(sender, config, "dispenser", "Dispenser Dupe",
                "Place a solid block right in front of a Dispenser, put items inside, and trigger it with Redstone.");

        // 4. Piston Shulker Dupe
        sendDupeInfo(sender, config, "piston_shulker", "Piston Shulker Dupe",
                "Place a Shulker Box in front of a Piston, then power the Piston to push into the Shulker Box.");

        // 5. Donkey Dupe
        sendDupeInfo(sender, config, "donkey", "Donkey Dupe",
                "Put items inside a Chested Donkey's inventory, then remove the Saddle from slot 0.");

        // 6. Explosion Dupe
        sendDupeInfo(sender, config, "explosion", "Explosion Dupe",
                "Fill any container (Chest, Barrel, Shulker Box) with items and blow it up using TNT or Creeper.");

        // 7. Portal Dupe
        sendDupeInfo(sender, config, "portal", "Portal Dupe",
                "Push a Chest Minecart or Chest Boat through a Nether or End Portal.");

        sender.sendMessage("§8§m-------------------------------------------");
        return true;
    }

    private void sendDupeInfo(CommandSender sender, FileConfiguration config, String key, String name, String instruction) {
        boolean enabled = config.getBoolean("dupes." + key + ".enabled", true);
        double chance = config.getDouble("dupes." + key + ".chance", 1.0) * 100.0;

        if (enabled) {
            sender.sendMessage("§e§l▶ " + name + " §7(§aACTIVE §7- §b" + String.format("%.0f", chance) + "% Rate§7)");
            sender.sendMessage("   §7Guide: §f" + instruction);
        } else {
            sender.sendMessage("§e§l▶ " + name + " §7(§cDISABLED§7)");
        }
    }
}