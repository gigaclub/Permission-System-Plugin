package net.gigaclub.permissionsystem.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import net.gigaclub.permissionsystem.Main;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCommand implements CommandExecutor, TabCompleter {
    Translation t = Main.getTranslation();
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
                    JSONArray permissions = group.getJSONArray("permissions");
                    sender.sendMessage(groupName);
                    for (int j = 0; j < permissions.length(); j++) {
                        sender.sendMessage(permissions.getString(j));
                    }
                    sender.sendMessage("______________________");
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PermissionSystem permissionSystem = Main.getPermissionSystem();
        JSONArray groups = permissionSystem.getAllGroups();

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("set");
            arguments.add("remove");
            arguments.add("list");
            return arguments;
        } else if (args.length == 2) {
            if (args[0].equals("set") || args[0].equals("remove")) {
                List<String> arguments = new ArrayList<>();
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    String groupName = group.getString("name");
                    arguments.add(groupName);
                }
                return arguments;
            }
        }
        return null;
    }
}
