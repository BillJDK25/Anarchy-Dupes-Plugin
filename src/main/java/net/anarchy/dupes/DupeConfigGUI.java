package net.anarchy.dupes.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class DupeConfigGUI {

    private final Plugin plugin;

    public DupeConfigGUI(Plugin plugin) {
        this.plugin = plugin;
    }

    public void openGUI(org.bukkit.entity.Player player) {
        Inventory gui = Bukkit.createInventory(new DupeGuiHolder(), 27, "§8[AnarchyDupes] Config Menu");
        FileConfiguration config = plugin.getConfig();

        // Decorative background glass pane
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }

        // Display 7 dupes
        gui.setItem(10, createDupeItem(config, "item_frame", Material.ITEM_FRAME, "§eItem Frame Dupe"));
        gui.setItem(11, createDupeItem(config, "cactus", Material.CACTUS, "§eCactus Dupe"));
        gui.setItem(12, createDupeItem(config, "dispenser", Material.DISPENSER, "§eDispenser Dupe"));
        gui.setItem(13, createDupeItem(config, "piston_shulker", Material.PISTON, "§ePiston Shulker Dupe"));
        gui.setItem(14, createDupeItem(config, "donkey", Material.SADDLE, "§eDonkey Dupe"));
        gui.setItem(15, createDupeItem(config, "explosion", Material.TNT, "§eExplosion Dupe"));
        gui.setItem(16, createDupeItem(config, "portal", Material.CHEST_MINECART, "§ePortal Dupe"));

        player.openInventory(gui);
    }

    public ItemStack createDupeItem(FileConfiguration config, String dupeKey, Material material, String displayName) {
        boolean enabled = config.getBoolean("dupes." + dupeKey + ".enabled", true);
        double chance = config.getDouble("dupes." + dupeKey + ".chance", 1.0) * 100.0;

        ItemStack item = new ItemStack(enabled ? material : Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            lore.add("§7Status: " + (enabled ? "§a[ENABLED]" : "§c[DISABLED]"));
            lore.add("§7Success Rate: §b" + String.format("%.0f", chance) + "%");
            lore.add("");
            lore.add("§e▶ Left Click: §fToggle Dupe");
            lore.add("§e▶ Right Click: §a+10% Chance");
            lore.add("§e▶ Shift + Right Click: §c-10% Chance");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}