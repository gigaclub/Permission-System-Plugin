package net.gigaclub.permissionsystem.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import net.gigaclub.permissionsystem.Main;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class GroupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //grepper minecraft paper get player by sender
        Player player = (Player) sender;
        //end grepper
        Translation t = Main.getTranslation();

        if (args.length == 0) {
            t.sendMessage("group.no.parameters", player);
            return true;
        }
        PermissionSystem permissionSystem = Main.getPermissionSystem();
        JSONArray groups = permissionSystem.getAllGroups();
        switch (args[0]) {
            case "list":
                // change after translation rework to support lists
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    String groupName = group.getString("name");
                    sender.sendMessage(groupName);
                }
                break;
            case "add":
                if (args.length < 3) {
                    t.sendMessage("group.add.no.parameters", player);
                    return true;
                }
                String groupName = args[1].toLowerCase();
                String playerName = args[2];
                //grepper minecraft paper get player by name
                Player playerToAdd = Bukkit.getPlayer(playerName);
                //end grepper
                int groupId = 0;
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    if (group.getString("name").toLowerCase().equals(groupName)) {
                        groupId = group.getInt("id");
                        break;
                    }
                }
                assert playerToAdd != null;
                permissionSystem.setGroups(playerToAdd.getUniqueId().toString(), Arrays.asList(groupId));
                Main.setupGroups();
                t.sendMessage("group.add.success", player);
                break;
            case "remove":
                if (args.length < 3) {
                    t.sendMessage("group.remove.no.parameters", player);
                    return true;
                }
                String groupNameToRemove = args[1].toLowerCase();
                String playerNameToRemove = args[2];
                Player playerToRemove = Bukkit.getPlayer(playerNameToRemove);
                groupId = 0;
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    if (group.getString("name").toLowerCase().equals(groupNameToRemove)) {
                        groupId = group.getInt("id");
                        break;
                    }
                }
                assert playerToRemove != null;
                permissionSystem.removeGroups(playerToRemove.getUniqueId().toString(), Arrays.asList(groupId));
                Main.setupGroups();
                t.sendMessage("group.remove.success", player);
                break;
        }
        return true;
    }
}
