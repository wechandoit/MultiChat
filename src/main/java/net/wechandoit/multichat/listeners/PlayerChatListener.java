package net.wechandoit.multichat.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.groups.MessageChannel;
import net.wechandoit.multichat.groups.groups.ShoutChannel;
import net.wechandoit.multichat.groups.groups.WhisperChannel;
import net.wechandoit.multichat.hooks.PlaceholderAPIHook;
import net.wechandoit.multichat.profiles.Profile;
import net.wechandoit.multichat.profiles.ProfileHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChatListener implements Listener {

    /**
     * The pattern for MiniMessage components.
     */
    public static final Pattern MINIMESSAGE_PATTERN = Pattern.compile("(?!<@)((?<start><)(?<token>[^<>]+(:(?<inner>['\"]?([^'\"](\\\\\\\\['\"])?)+['\"]?))*)(?<end>>))+?");

    private ProfileHandler profileHandler = Multichat.getProfileHandler();

    /*
     * Since we are using MiniMessage and Adventure
     * Use https://webui.adventure.kyori.net/ for formatting
     * Usage: https://docs.adventure.kyori.net/minimessage/api.html
     * Formatting: https://docs.adventure.kyori.net/minimessage/format.html
     */

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // get profile
        Profile profile = profileHandler.getProfile(player);
        if (profile == null) return;

        // remove formatting
        String message = stripMiniTokens(event.getMessage());
        if (StringUtils.deleteWhitespace(message).equals("")) {
            event.setCancelled(true);
            return;
        }

        // get chat group
        String chatGroupString = profile.getChatGroup();

        if (!Multichat.getMessageGroupHandler().has(chatGroupString)) return;

        MessageChannel chatGroup = Multichat.getMessageGroupHandler().get(chatGroupString);
        List<Profile> receivers = new ArrayList<>();
        event.setCancelled(true);
        Player otherPlayer = null;

        if (chatGroup instanceof WhisperChannel) {
            otherPlayer = Bukkit.getPlayer(profile.getLastMsgdUUID());
            if (otherPlayer == null) return; // broken?

            Profile otherProfile = profileHandler.getProfile(otherPlayer);
            if (otherProfile == null) profileHandler.addProfile(otherPlayer);

            if (!chatGroup.isGlobal() && chatGroup.getBlockRange() > 1 && !profileHandler.getNearbyProfilesByRange(player.getUniqueId(), chatGroup.getBlockRange()).contains(otherProfile)) {
                profile.sendMessage("<red>You are not within range to msg this player.");
                return;
            }

            receivers.add(profile);
            receivers.add(profileHandler.getProfile(otherPlayer));

        } else if (chatGroup instanceof ShoutChannel) {
            receivers.addAll(profileHandler.getProfilesInWorld(player.getWorld()));
        } else {
            if (chatGroup.isGlobal()) {
                receivers.addAll(profileHandler.getOnlineProfiles());
            } else {
                if (!chatGroup.isGlobal() && chatGroup.getBlockRange() > 1)
                    receivers.addAll(profileHandler.getNearbyProfilesByRange(player.getUniqueId(), chatGroup.getBlockRange()));
                else receivers.addAll(profileHandler.getOnlineProfiles());
            }
        }

        String chatFormat = chatGroup.getChatFormat().replaceAll("%name%", ConfigSettings.rankPrefixMap.get(profile.getRank()));

        // PlaceholderAPI support
        if (Multichat.hasPAPI) {
            chatFormat = PlaceholderAPIHook.parsePAPIPlaceholders(player, chatFormat);
        }
        sendMessageToPlayers(receivers, chatFormat, message, player, otherPlayer);
    }

    public static void sendMessageToPlayers(List<Profile> players, String format, String message, Player player, Player other) {
        for (Profile p : players) {
            TagResolver placeholders = TagResolver.resolver(Placeholder.parsed("name", player.getName()), Placeholder.parsed("message", p.hasProfanityFilter() ? Multichat.getFilter().filterBadWords(message) : message), Placeholder.parsed("other", other != null ? other.getName() : ""));

            Component parsed = Multichat.mm.deserialize(format, placeholders);
            p.sendMessage(parsed);
        }

        Multichat.logger.info(player.getName() + ": " + message);

    }

    /**
     * Strips the given String of mini tokens formatted tags eg. {@code <blue>} or {@code <anything>}.
     *
     * @param text the given String to strip mini tokens from
     * @return the given String with mini tokens stripped
     */
    public static String stripMiniTokens(String text) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = MINIMESSAGE_PATTERN.matcher(text);

        int lastEnd;
        int endIndex;
        for (lastEnd = 0; matcher.find(); lastEnd = endIndex) {
            int startIndex = matcher.start();
            endIndex = matcher.end();
            if (startIndex > lastEnd) {
                sb.append(text, lastEnd, startIndex);
            }
        }

        if (text.length() > lastEnd) {
            sb.append(text.substring(lastEnd));
        }

        return sb.toString();
    }


}
