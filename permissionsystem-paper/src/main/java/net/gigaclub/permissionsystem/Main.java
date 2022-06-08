package net.gigaclub.permissionsystem;

import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import de.dytanic.cloudnet.driver.permission.PermissionUserGroupInfo;
import net.gigaclub.permissionsystem.commands.GroupCommand;
import net.gigaclub.permissionsystem.commands.SyncCommand;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Collection;
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
        PermissionSystem permissionSystem = Main.getPermissionSystem();

        JSONArray groups = permissionSystem.getAllGroups();
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

        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (Player player : players) {
            JSONArray groupsOfPlayer = permissionSystem.getGroups(player.getUniqueId().toString());
            permissionManagement.modifyUser(player.getUniqueId(), user -> {
                Collection<PermissionUserGroupInfo> groupsOfUser = user.getGroups();
                for (PermissionUserGroupInfo group : groupsOfUser) {
                    user.removeGroup(group.getGroup());
                }
                for (int i = 0; i < groupsOfPlayer.length(); i++) {
                    JSONObject group = groupsOfPlayer.getJSONObject(i);
                    String groupName = group.getString("name");
                    user.addGroup(groupName);
                }
            });
        }

    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("syncgroups")).setExecutor(new SyncCommand());
        Objects.requireNonNull(getCommand("group")).setExecutor(new GroupCommand());
    }

}
