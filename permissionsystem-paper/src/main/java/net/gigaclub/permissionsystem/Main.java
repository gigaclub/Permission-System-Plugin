package net.gigaclub.permissionsystem;

import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import org.bukkit.plugin.java.JavaPlugin;

import de.dytanic.cloudnet.driver.CloudNetDriver;

public final class Main extends JavaPlugin {

    private static Main plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

}
