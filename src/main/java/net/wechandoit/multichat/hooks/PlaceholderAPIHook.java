package net.wechandoit.multichat.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {

    public static String parsePAPIPlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }


}
