package net.wechandoit.multichat.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LuckPermsHook {

    private static LuckPerms api = LuckPermsProvider.get();

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }

    public static Set<Group> getGroupObjects() {
        return api.getGroupManager().getLoadedGroups();
    }

    public static List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (Group group : getGroupObjects()) {
            groups.add(group.getName());
        }
        return groups;
    }

    public static CompletableFuture<Boolean> isAdmin(UUID who) {
        return api.getUserManager().loadUser(who)
                .thenApplyAsync(user -> {
                    Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                    return inheritedGroups.stream().anyMatch(g -> g.getName().equals("admin"));
                });
    }

}
