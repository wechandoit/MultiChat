package net.wechandoit.multichat.commands;

import com.google.common.base.Joiner;
import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.groups.MessageChannel;
import net.wechandoit.multichat.hooks.PlaceholderAPIHook;
import net.wechandoit.multichat.listeners.PlayerChatListener;
import net.wechandoit.multichat.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PrivateReplyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Multichat.getProfileHandler().getProfile(player);

            if (profile.getLastMsgdUUID() != null) {

                if (Bukkit.getPlayer(profile.getLastMsgdUUID()) != player) {

                    MessageChannel chatGroup = Multichat.getMessageGroupHandler().get("private");
                    List<Profile> receivers = new ArrayList<>();

                    Player otherPlayer = Bukkit.getPlayer(profile.getLastMsgdUUID());
                    profile.setLastMsgdUUID(otherPlayer.getUniqueId());

                    Profile otherProfile = Multichat.getProfileHandler().getProfile(otherPlayer);
                    if (otherProfile == null)
                        Multichat.getProfileHandler().addProfile(otherPlayer);
                    if (!chatGroup.isGlobal() && chatGroup.getBlockRange() > 1 && !Multichat.getProfileHandler().getNearbyProfilesByRange(player.getUniqueId(), chatGroup.getBlockRange()).contains(otherProfile)) {
                        profile.sendMessage("<red>You are not within range to msg this player.");
                        return true;
                    }

                    profile.setLastMsgdUUID(otherPlayer.getUniqueId());
                    otherProfile.setLastMsgdUUID(player.getUniqueId());

                    receivers.add(profile);
                    receivers.add(Multichat.getProfileHandler().getProfile(otherPlayer));

                    String chatFormat = chatGroup.getChatFormat().replaceAll("%name%", ConfigSettings.rankPrefixMap.get(profile.getRank()));

                    // PlaceholderAPI support
                    if (Multichat.hasPAPI) {
                        chatFormat = PlaceholderAPIHook.parsePAPIPlaceholders(player, chatFormat);
                    }
                    PlayerChatListener.sendMessageToPlayers(receivers, chatFormat, Joiner.on(" ").join(args), player, otherPlayer);
                } else {
                    profile.sendMessage("<red>You cannot message yourself!");
                }
            } else {
                profile.sendMessage("<red>" + Bukkit.getOfflinePlayer(profile.getLastMsgdUUID()).getName() + " is offline!");
            }

        }
        return true;
    }
}
