package net.anarchy.dupes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DupeEventListener implements Listener {

    private final Plugin plugin;
    private final NamespacedKey playerDroppedKey;

    public DupeEventListener(Plugin plugin) {
        this.plugin = plugin;
        this.playerDroppedKey = new NamespacedKey(plugin, "player_dropped_item");
    }

    /**
     * Helper method to check if a dupe is enabled and passes the random chance check.
     */
    private boolean isDupeTriggered(String dupeKey, double defaultChance) {
        if (!plugin.getConfig().getBoolean("dupes." + dupeKey + ".enabled", true)) {
            return false;
        }
        double chance = plugin.getConfig().getDouble("dupes." + dupeKey + ".chance", defaultChance);
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    // ==========================================
    // 1. ITEM FRAME & GLOW ITEM FRAME DUPE (Left-Click Only)
    // ==========================================

    // Đã xóa onItemFrameInteract (Chuột phải) để việc đặt vật phẩm vào khung diễn ra bình thường.

    @EventHandler
    public void onItemFrameHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame frame)) return;
        if (!(event.getDamager() instanceof Player)) return;

        ItemStack containedItem = frame.getItem();

        // Chỉ tính dupe khi khung đang chứa vật phẩm và người chơi ĐẤM (Chuột trái) vào khung
        if (containedItem.getType() != Material.AIR) {
            if (isDupeTriggered("item_frame", 0.25)) {
                event.setCancelled(true); // Hủy sự kiện đánh mặc định của Minecraft

                // Nhân đôi vật phẩm trong khung ($x2$)
                ItemStack dupeItem = containedItem.clone();
                dupeItem.setAmount(2);
                frame.getWorld().dropItemNaturally(frame.getLocation(), dupeItem);

                // Rớt ra khung Item Frame / Glow Item Frame tương ứng
                Material frameType = (frame instanceof GlowItemFrame) ? Material.GLOW_ITEM_FRAME : Material.ITEM_FRAME;
                frame.getWorld().dropItemNaturally(frame.getLocation(), new ItemStack(frameType));

                // Xóa thực thể khung khỏi thế giới
                frame.remove();
            }
            // Nếu không trúng % dupe: Để mặc định Vanilla (món đồ rớt ra x1 bình thường)
        }
    }

    // ==========================================
    // 2. CACTUS DUPE (Only for player-dropped items)
    // ==========================================
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item itemEntity = event.getItemDrop();
        itemEntity.getPersistentDataContainer().set(playerDroppedKey, PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler
    public void onCactusDamageItem(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof Item itemEntity)) return;

        if (event.getDamager() != null && event.getDamager().getType() == Material.CACTUS) {
            if (!itemEntity.getPersistentDataContainer().has(playerDroppedKey, PersistentDataType.BYTE)) {
                return;
            }

            itemEntity.getPersistentDataContainer().remove(playerDroppedKey);

            if (isDupeTriggered("cactus", 0.30)) {
                event.setCancelled(true);

                Vector throwVector = new Vector(
                        ThreadLocalRandom.current().nextDouble(-0.3, 0.3),
                        0.35,
                        ThreadLocalRandom.current().nextDouble(-0.3, 0.3)
                );

                itemEntity.setVelocity(throwVector);

                Item clonedItem = itemEntity.getWorld().dropItem(itemEntity.getLocation(), itemEntity.getItemStack().clone());
                clonedItem.setVelocity(throwVector);
            }
        }
    }

    // ==========================================
    // 3. DISPENSER DUPE (When front face is blocked)
    // ==========================================
    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) return;

        if (block.getBlockData() instanceof Directional directional) {
            Block targetBlock = block.getRelative(directional.getFacing());

            if (targetBlock.getType().isSolid()) {
                event.setCancelled(true);

                ItemStack itemToDispense = event.getItem();
                if (itemToDispense.getType() == Material.AIR) return;

                if (isDupeTriggered("dispenser", 0.50)) {
                    Location dropBelowLocation = block.getLocation().add(0.5, -0.5, 0.5);
                    ItemStack duplicatedItem = itemToDispense.clone();
                    duplicatedItem.setAmount(1);
                    block.getWorld().dropItemNaturally(dropBelowLocation, duplicatedItem);
                }
            }
        }
    }

    // ==========================================
    // 4. PISTON SHULKER DUPE
    // ==========================================
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (org.bukkit.Tag.SHULKER_BOXES.isTagged(block.getType())) {
                if (isDupeTriggered("piston_shulker", 1.0)) {
                    event.setCancelled(true);

                    if (block.getState() instanceof ShulkerBox shulkerState) {
                        ItemStack shulkerItem = new ItemStack(block.getType());
                        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();

                        if (meta != null) {
                            meta.setBlockState(shulkerState);
                            shulkerItem.setItemMeta(meta);

                            block.getWorld().dropItemNaturally(
                                    block.getLocation().add(0.5, 0.5, 0.5),
                                    shulkerItem
                            );
                        }
                    }
                    break;
                }
            }
        }
    }

    // ==========================================
    // 5. DONKEY DUPE (Saddle removal via Click or Q / Ctrl+Q keys)
    // ==========================================
    @EventHandler
    public void onDonkeySaddleRemove(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Donkey donkey)) return;
        if (!donkey.isTamed() || !donkey.isCarryingChest()) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getInventory())) return;

        if (event.getSlot() == 0) {
            ItemStack currentSaddle = event.getCurrentItem();
            if (currentSaddle != null && currentSaddle.getType() == Material.SADDLE) {
                switch (event.getAction()) {
                    case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME,
                         MOVE_TO_OTHER_INVENTORY, SWAP_WITH_CURSOR, HOTBAR_SWAP,
                         DROP_ONE_SLOT, DROP_ALL_SLOT -> {

                        if (!isDupeTriggered("donkey", 1.0)) return;

                        Inventory donkeyInv = event.getInventory();
                        for (int i = 1; i < donkeyInv.getSize(); i++) {
                            ItemStack item = donkeyInv.getItem(i);
                            if (item != null && item.getType() != Material.AIR) {
                                donkey.getWorld().dropItemNaturally(donkey.getLocation(), item.clone());
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // 6. EXPLOSION CONTAINER DUPE (Supports all container blocks)
    // ==========================================
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosionDupes(event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosionDupes(event.blockList());
    }

    private void handleExplosionDupes(List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getState() instanceof Container container) {
                if (isDupeTriggered("explosion", 0.50)) {
                    Location loc = block.getLocation().add(0.5, 0.5, 0.5);

                    // Case A: Shulker Box -> Drops an additional Shulker Box item with full contents
                    if (container instanceof ShulkerBox shulkerState) {
                        ItemStack shulkerItem = new ItemStack(block.getType());
                        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();

                        if (meta != null) {
                            meta.setBlockState(shulkerState);
                            shulkerItem.setItemMeta(meta);
                            block.getWorld().dropItemNaturally(loc, shulkerItem);
                        }
                    }
                    // Case B: Regular Containers (Chest, Barrel, Hopper, Dispenser, Furnace, etc.)
                    else {
                        // 1. Drop 1 container block item
                        block.getWorld().dropItemNaturally(loc, new ItemStack(block.getType()));

                        // 2. Clone and drop all contents onto the ground
                        for (ItemStack item : container.getInventory().getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                block.getWorld().dropItemNaturally(loc, item.clone());
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // 7. PORTAL VEHICLE DUPE (Chest Minecart & Chest Boat)
    // ==========================================
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        // Option 1: Chest Minecart
        if (event.getEntity() instanceof StorageMinecart minecart) {
            if (isDupeTriggered("portal", 0.50)) {
                Location loc = minecart.getLocation();

                loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.CHEST_MINECART));

                for (ItemStack item : minecart.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        loc.getWorld().dropItemNaturally(loc, item.clone());
                    }
                }
            }
        }
        // Option 2: Chest Boat
        else if (event.getEntity() instanceof ChestBoat chestBoat) {
            if (isDupeTriggered("portal", 0.50)) {
                Location loc = chestBoat.getLocation();

                Material boatMat;
                try {
                    boatMat = Material.valueOf(chestBoat.getBoatType().name() + "_CHEST_BOAT");
                } catch (Exception e) {
                    boatMat = Material.OAK_CHEST_BOAT;
                }

                loc.getWorld().dropItemNaturally(loc, new ItemStack(boatMat));

                for (ItemStack item : chestBoat.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        loc.getWorld().dropItemNaturally(loc, item.clone());
                    }
                }
            }
        }
    }
}