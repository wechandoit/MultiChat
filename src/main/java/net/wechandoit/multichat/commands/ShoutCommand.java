package net.wechandoit.multichat.commands;

import com.google.common.base.Joiner;
import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.groups.MessageChannel;
import net.wechandoit.multichat.hooks.PlaceholderAPIHook;
import net.wechandoit.multichat.listeners.PlayerChatListener;
import net.wechandoit.multichat.profiles.Profile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShoutCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("multichat.shout") || player.isOp()) {
                Profile profile = Multichat.getProfileHandler().getProfile(player);

                System.out.println(Multichat.getMessageGroupHandler().getShoutChannelForWorld(profile));
                MessageChannel chatGroup = Multichat.getMessageGroupHandler().get(Multichat.getMessageGroupHandler().getShoutChannelForWorld(profile));
                List<Profile> receivers = new ArrayList<>();

                String chatFormat = chatGroup.getChatFormat().replaceAll("%name%", ConfigSettings.rankPrefixMap.get(profile.getRank()));

                // PlaceholderAPI support
                if (Multichat.hasPAPI) {
                    chatFormat = PlaceholderAPIHook.parsePAPIPlaceholders(player, chatFormat);
                }
                receivers.addAll(Multichat.getProfileHandler().getProfilesInWorld(player.getWorld()));
                PlayerChatListener.sendMessageToPlayers(receivers, chatFormat, Joiner.on(" ").join(args), player, null);
            }
        }

        return true;

    }
}
