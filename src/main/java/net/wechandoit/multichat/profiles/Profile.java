package net.wechandoit.multichat.profiles;

import net.kyori.adventure.text.Component;
import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.hooks.LuckPermsHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Profile {
    private String chatGroup;
    private UUID uuid;
    private boolean profanityFilter;
    private UUID lastMsgdUUID;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.profanityFilter = false;
        // set chatGroup to the default set in config
        chatGroup = ConfigSettings.getDefaultChatGroup;
        this.lastMsgdUUID = null;
    }

    public Profile(UUID uuid, boolean profanityFilter) {
        this.uuid = uuid;
        this.profanityFilter = profanityFilter;
        // set chatGroup to the default set in config
        chatGroup = ConfigSettings.getDefaultChatGroup;
        this.lastMsgdUUID = null;
    }

    public Profile(UUID uuid, boolean profanityFilter, String chatGroup) {
        this.uuid = uuid;
        this.profanityFilter = profanityFilter;
        this.chatGroup = chatGroup;
        this.lastMsgdUUID = null;
    }

    public boolean hasProfanityFilter() {
        return profanityFilter;
    }

    public void setProfanityFilter(boolean profanityFilter) {
        this.profanityFilter = profanityFilter;
    }

    public void sendMessage(Component component) {
        Bukkit.getPlayer(uuid).sendMessage(component);
    }

    public void sendMessage(String message) {
        Component parsed = Multichat.mm.deserialize(message);
        sendMessage(parsed);
    }

    public String getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(String chatGroup) {
        this.chatGroup = chatGroup;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getLastMsgdUUID() {
        return lastMsgdUUID;
    }

    public void setLastMsgdUUID(UUID lastMsgdUUID) {
        this.lastMsgdUUID = lastMsgdUUID;
    }

    public String getRank() {
        if (Multichat.hasLuckPerms) {
            return LuckPermsHook.getPlayerGroup(getPlayer(), LuckPermsHook.getGroups());
        } else if (getPlayer().isOp()) {
            return "admin";
        } else {
            return "default";
        }
    }
}
