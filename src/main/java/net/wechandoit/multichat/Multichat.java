package net.wechandoit.multichat;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.wechandoit.multichat.commands.ChatCommand;
import net.wechandoit.multichat.commands.PrivateMessageCommand;
import net.wechandoit.multichat.commands.PrivateReplyCommand;
import net.wechandoit.multichat.commands.ShoutCommand;
import net.wechandoit.multichat.config.ConfigSettingsHandler;
import net.wechandoit.multichat.groups.MessageChannelHandler;
import net.wechandoit.multichat.listeners.PlayerChatListener;
import net.wechandoit.multichat.listeners.PlayerJoinListener;
import net.wechandoit.multichat.profiles.ProfileHandler;
import net.wechandoit.multichat.utils.ProfanityFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Multichat extends JavaPlugin implements Listener {

    private static Multichat plugin;
    private static ProfileHandler profileHandler;
    private static MessageChannelHandler messageChannelHandler;
    private static ConfigSettingsHandler configSettingsHandler;
    private static ProfanityFilter filter; // https://github.com/lyenliang/Profanity-Filter/

    public static boolean hasLuckPerms = false;
    public static boolean hasPAPI = false;
    public static MiniMessage mm = MiniMessage.miniMessage();
    public static Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {

        plugin = this;

        // initialize profanity filter
        filter = new ProfanityFilter();

        // Config logic
        saveDefaultConfig();
        configSettingsHandler = new ConfigSettingsHandler();

        // Plugin startup logic
        messageChannelHandler = new MessageChannelHandler();
        profileHandler = new ProfileHandler();

        registerListeners();
        registerHooks();
        loadCommands();

        logger.info("Multichat Loaded!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    private void registerHooks() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null && getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            hasLuckPerms = true;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hasPAPI = true;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void loadCommands() {
        getCommand("multichat").setExecutor(new ChatCommand());
        getCommand("shout").setExecutor(new ShoutCommand());
        getCommand("message").setExecutor(new PrivateMessageCommand());
        getCommand("reply").setExecutor(new PrivateReplyCommand());
    }

    public static ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    public static MessageChannelHandler getMessageGroupHandler() {
        return messageChannelHandler;
    }

    public static ProfanityFilter getFilter() {
        return filter;
    }

    public static Multichat getInstance() {
        return plugin;
    }
}
