package net.gigaclub.permissionsystem;

import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import net.gigaclub.permissionsystem.commands.SyncCommand;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Translation translation;
    private static PermissionSystem permissionSystem;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);

        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));
        translation.setCategory("permissionsystem");

        setPermissionSystem(new PermissionSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        Main.setupGroups();

        this.registerCommands();

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

    public static Translation getTranslation() {
        return Main.translation;
    }

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
    }

    public static PermissionSystem getPermissionSystem() {
        return Main.permissionSystem;
    }

    public static void setPermissionSystem(PermissionSystem permissionSystem) {
        Main.permissionSystem = permissionSystem;
    }

    public static void setupGroups() {
        IPermissionManagement permissionManagement = CloudNetDriver.getInstance().getPermissionManagement();

        JSONArray groups = Main.getPermissionSystem().getAllGroups();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            String groupName = group.getString("name");
            JSONArray permissions = group.getJSONArray("permissions");
            permissionManagement.addGroup(groupName, 0);
            permissionManagement.modifyGroup(groupName, permissionGroup -> {
                for (int j = 0; j < permissions.length(); j++) {
                    permissionGroup.addPermission(permissions.getString(j));
                }
            });
        }
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("syncgroups")).setExecutor(new SyncCommand());
    }

}
