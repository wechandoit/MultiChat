package net.wechandoit.multichat.config;

import net.wechandoit.multichat.Multichat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ConfigSettingsHandler {

    public static File file = new File("plugins/Multichat/", "config.yml");
    public static YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

    public ConfigSettingsHandler() {
        loadConfig();
    }

    public void loadConfig() {
        // get default group
        ConfigSettings.getDefaultChatGroup = data.getString("settings.default-group");

        // get default chat format
        ConfigSettings.defaultChatFormat = data.getString("settings.default-chat-format");

        // check if profanity should be enabled when a player first joins
        ConfigSettings.enableProfanityFilterOnFirstJoin = data.getBoolean("settings.default-profanity-filter");

        // check if debug messages should be shown
        ConfigSettings.allowDebugMessages = data.getBoolean("settings.allow-debug-messages");

        // get bad words list and add to the actual filter
        List<String> badwords = data.getStringList("settings.bad-words-list");
        if (Multichat.getFilter() != null) {
            Multichat.getFilter().buildDictionaryTree(badwords);
        }

        // get rank prefixes
        ConfigSettings.rankPrefixMap = new HashMap<>();
        if (data.getConfigurationSection("rank-prefixes") != null) {
            for (String rank : data.getConfigurationSection("rank-prefixes").getKeys(false)) {
                ConfigSettings.rankPrefixMap.put(rank, data.getString("rank-prefixes." + rank));
            }
        }

        Multichat.logger.info("[Multichat] Config Settings Loaded!");
    }

}
