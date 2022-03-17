package net.wechandoit.multichat.listeners;

import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.profiles.ProfileHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ProfileHandler profileHandler = Multichat.getProfileHandler();
        Player player = event.getPlayer();

        boolean canAdd = profileHandler.addProfile(player);
        if (canAdd) {
            Multichat.logger.info(player.getUniqueId() + " added to chat database");
        }
    }
}
