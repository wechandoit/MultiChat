package net.wechandoit.multichat.groups;

import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettingsHandler;
import net.wechandoit.multichat.groups.groups.WhisperChannel;
import net.wechandoit.multichat.groups.groups.ShoutChannel;
import net.wechandoit.multichat.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageChannelHandler {
    private List<MessageChannel> messageChannelList = new ArrayList<>();
    private YamlConfiguration data = ConfigSettingsHandler.data;

    public MessageChannelHandler() {
        load();
    }

    public void load() {

        if (data.getConfigurationSection("groups") != null) {
            for (String rank : data.getConfigurationSection("groups").getKeys(false)) {
                MessageChannel channel;
                if (rank.contains("private")) {
                    channel = new WhisperChannel(rank, data.getString("groups." + rank + ".chat-format"), false, data.getDouble("groups." + rank + ".block-range"));
                    messageChannelList.add(channel);
                } else if (rank.contains("shout")) {
                    for (World w : Bukkit.getWorlds()) {
                        channel = new ShoutChannel(rank + "-" + w.getName(), data.getString("groups." + rank + ".chat-format"), false, w);
                        messageChannelList.add(channel);
                    }
                } else {
                    if (data.getDouble("groups." + rank + ".block-range") > 1) {
                        channel = new MessageChannel(rank, data.getString("groups." + rank + ".chat-format"), data.getBoolean("groups." + rank + ".global"), data.getBoolean("groups." + rank + ".default"), data.getDouble("groups." + rank + ".block-range"));
                    } else {
                        channel = new MessageChannel(rank, data.getString("groups." + rank + ".chat-format"), data.getBoolean("groups." + rank + ".global"));
                    }
                    messageChannelList.add(channel);
                }
            }
        }
    }

    public void addChannelSettingsToConfig(MessageChannel channel) {
        data.set("groups." + channel.getName() + ".chat-format", channel.getChatFormat());
        data.set("groups." + channel.getName() + ".global", channel.isGlobal());
        data.set("groups." + channel.getName() + ".default", channel.isGlobal());
        data.set("groups." + channel.getName() + ".block-range", channel.getBlockRange());

        try {
            data.save(ConfigSettingsHandler.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<MessageChannel> getMessageGroupList() {
        return messageChannelList;
    }

    public boolean isValidChannel(String chatGroup) {
        if (messageChannelList == null || messageChannelList.isEmpty())
            return false;
        for (MessageChannel channel : messageChannelList) {
            if (channel.getName().equalsIgnoreCase(chatGroup)) return true;
        }
        return false;
    }

    public List<String> getAllChannelNames() {
        List<String> channelNames = new ArrayList<>();
        if (messageChannelList == null || messageChannelList.isEmpty())
            return channelNames;
        for (MessageChannel channel : messageChannelList)
            channelNames.add(channel.getName());
        return channelNames;
    }

    public List<String> getValidChannelNamesForProfile(Profile profile) {
        List<String> channelNames = new ArrayList<>();
        if (profile.getPlayer() != null) {
            if (messageChannelList == null || messageChannelList.isEmpty())
                return channelNames;
            for (MessageChannel channel : messageChannelList) {
                if (!channel.getName().contains("shout"))
                    channelNames.add(channel.getName());
                if (channel.getName().split("-")[0].equals("shout") && profile.getPlayer().hasPermission("multichat.admin"))
                    channelNames.add(channel.getName());
            }
        }
        return channelNames;
    }

    public String getShoutChannelForWorld(Profile profile) {
        String channelName = "";
        if (messageChannelList == null || messageChannelList.isEmpty())
            return channelName;
        for (MessageChannel channel : messageChannelList)
            if (channel.getName().contains("shout") && channel.getName().split("-")[1].equals(profile.getPlayer().getWorld().getName())) {
                channelName = channel.getName();
            }
        return channelName;
    }

    public boolean isChannelNameBlacklisted(String name) {
        if (Multichat.getFilter().filterBadWords(name).contains("*") || name.split("-")[0].equals("shout") || getAllChannelNames().contains(name))
            return true;
        return false;
    }

    public boolean isChannelDeleteable(String name) {
        if (name.split("-")[0].equals("shout") || name.equals("global") || name.equals("private") || name.equals("local"))
            return false;
        return true;
    }

    public MessageChannel get(String chatGroup) {
        if (!has(chatGroup)) return null;
        for (MessageChannel group : messageChannelList) {
            if (group.getName().equals(chatGroup)) return group;
        }
        return null;
    }

    public boolean has(String chatGroup) {
        if (messageChannelList == null || messageChannelList.isEmpty())
            return false;
        return messageChannelList.stream().anyMatch(group -> group.getName().equals(chatGroup));
    }
}
