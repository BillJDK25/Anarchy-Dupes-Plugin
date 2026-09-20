package net.anarchy.dupes.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class DupeGuiListener implements Listener {

    private final Plugin plugin;
    private final DupeConfigGUI gui;
    private final Map<Integer, String> slotMap = new HashMap<>();

    public DupeGuiListener(Plugin plugin) {
        this.plugin = plugin;
        this.gui = new DupeConfigGUI(plugin);

        // Ánh xạ Slot tương ứng với Key trong config.yml
        slotMap.put(10, "item_frame");
        slotMap.put(11, "cactus");
        slotMap.put(12, "dispenser");
        slotMap.put(13, "piston_shulker");
        slotMap.put(14, "donkey");
        slotMap.put(15, "explosion");
        slotMap.put(16, "portal");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof DupeGuiHolder) {
            event.setCancelled(true); // Ngăn người chơi lấy item ra ngoài

            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getSlot();
            if (!slotMap.containsKey(slot)) return;

            String dupeKey = slotMap.get(slot);
            FileConfiguration config = plugin.getConfig();

            boolean enabled = config.getBoolean("dupes." + dupeKey + ".enabled", true);
            double chance = config.getDouble("dupes." + dupeKey + ".chance", 1.0);

            ClickType click = event.getClick();

            // Click Trái: Toggle Bật/Tắt
            if (click.isLeftClick()) {
                config.set("dupes." + dupeKey + ".enabled", !enabled);
            }
            // Click Phải: Tăng 10%
            else if (click == ClickType.RIGHT) {
                chance = Math.min(1.0, chance + 0.10);
                config.set("dupes." + dupeKey + ".chance", Math.round(chance * 100.0) / 100.0);
            }
            // Shift + Click Phải: Giảm 10%
            else if (click == ClickType.SHIFT_RIGHT) {
                chance = Math.max(0.0, chance - 0.10);
                config.set("dupes." + dupeKey + ".chance", Math.round(chance * 100.0) / 100.0);
            }

            // Lưu thay đổi vào file config.yml và làm mới GUI
            plugin.saveConfig();
            gui.openGUI(player);
        }
    }
}