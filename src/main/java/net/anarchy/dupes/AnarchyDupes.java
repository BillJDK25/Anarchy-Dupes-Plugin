package net.anarchy.dupes;

import net.anarchy.dupes.command.AnarchyDupesCommand;
import net.anarchy.dupes.command.DupesHelpCommand;
import net.anarchy.dupes.gui.DupeGuiListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnarchyDupes extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register Event Listeners
        getServer().getPluginManager().registerEvents(new DupeEventListener(this), this);
        getServer().getPluginManager().registerEvents(new DupeGuiListener(this), this);

        // Register Commands
        if (getCommand("Anarchydupes") != null) {
            getCommand("Anarchydupes").setExecutor(new AnarchyDupesCommand(this));
        }

        if (getCommand("Dupes") != null) {
            getCommand("Dupes").setExecutor(new DupesHelpCommand(this));
        }

        getLogger().info("AnarchyDupes!");
    }
}