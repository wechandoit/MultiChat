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
import java.util.Arrays;
import java.util.List;

public class PrivateMessageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Multichat.getProfileHandler().getProfile(player);
            if (args.length > 1) {
                if (Bukkit.getPlayer(args[0]) != null) {

                    if (Bukkit.getPlayer(args[0]) != player) {

                        MessageChannel chatGroup = Multichat.getMessageGroupHandler().get("private");
                        List<Profile> receivers = new ArrayList<>();

                        Player otherPlayer = Bukkit.getPlayer(args[0]);
                        profile.setLastMsgdUUID(otherPlayer.getUniqueId());

                        Profile otherProfile = Multichat.getProfileHandler().getProfile(otherPlayer);
                        if (otherProfile == null)
                            Multichat.getProfileHandler().addProfile(otherPlayer);
                        if (!chatGroup.isGlobal() && chatGroup.getBlockRange() > 1 && !Multichat.getProfileHandler().getNearbyProfilesByRange(player.getUniqueId(), chatGroup.getBlockRange()).contains(otherProfile)) {
                            profile.sendMessage("<red>Get within " + chatGroup.getBlockRange() + " blocks of " + otherProfile.getPlayer().getName() + " to message them.");
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
                        PlayerChatListener.sendMessageToPlayers(receivers, chatFormat, Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)), player, otherPlayer);
                    } else {
                        profile.sendMessage("<red>You cannot message yourself!");
                    }
                } else {
                    profile.sendMessage("<red>" + args[0] + " is offline!");
                }
            } else {
                profile.sendMessage("<red>Usage: /msg [player] [message]");
            }
        }
        return true;
    }
}
