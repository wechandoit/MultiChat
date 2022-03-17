package net.wechandoit.multichat.profiles;

import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.database.Database;
import net.wechandoit.multichat.database.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProfileHandler {

    private Database db;
    private List<Profile> profileList = new ArrayList<>();


    public ProfileHandler() {
        // load profiles on start from file/database
        loadDB();
    }

    private void loadDB() {
        db = new SQLite(Multichat.getInstance());
        db.load();
        loadProfileObjects();
    }

    public List<Profile> getProfileList() {
        return profileList;
    }

    public boolean addProfile(Player player) {
        return addProfile(player.getUniqueId());
    }

    public boolean addProfile(UUID uuid) {
        if (isUUIDRegistered(uuid)) return false;

        Profile profile = new Profile(uuid);
        boolean added = insertInDb(profile);

        profileList.add(profile);

        if (added && ConfigSettings.allowDebugMessages) {
            Multichat.logger.info("[MultiChat] UUID " + uuid + " added to the database!");
        }

        return true;
    }

    public Database getDb() {
        return db;
    }

    public boolean insertInDb(Profile profile) {
        try {
            Statement statement = db.getSQLConnection().createStatement();
            String sql = "INSERT or IGNORE into profiles (uuid, chatGroup, hasProfanityFilter) VALUES ('"
                    + profile.getUUID().toString() + "', '" + profile.getChatGroup() + "', '" + profile.hasProfanityFilter() + "');";
            statement.execute(sql);

            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean updateInDb(Profile profile) {
        try {
            Statement statement = db.getSQLConnection().createStatement();
            Multichat.logger.info(profile.hasProfanityFilter() + " " + profile.getChatGroup());
            statement.execute("UPDATE profiles set hasProfanityFilter = " + profile.hasProfanityFilter() + " where uuid='" + profile.getUUID().toString() + "';");
            statement.execute("UPDATE profiles set chatGroup = '" + profile.getChatGroup() + "' where uuid='" + profile.getUUID().toString() + "';");
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void loadProfileObjects() {
        try {
            Statement statement = db.getSQLConnection().createStatement();
            String sql = "SELECT * FROM profiles";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));

                Profile profile = new Profile(UUID.fromString(rs.getString("uuid")), rs.getBoolean("hasProfanityFilter"), rs.getString("chatGroup"));
                if (!isUUIDRegistered(uuid)) {
                    profileList.add(profile);
                }

            }
            Multichat.logger.info("Loaded " + profileList.size() + " profiles!");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Profile> getNearbyProfilesByRange(Player player, double range) {
        return getNearbyProfilesByRange(player.getUniqueId(), range);
    }

    public List<Profile> getNearbyProfilesByRange(UUID uuid, double range) {
        List<Profile> nearbyProfiles = new ArrayList<>();

        for (Profile p : profileList) {
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(p.getUUID()) != null && Bukkit.getPlayer(uuid).getLocation().distanceSquared(Bukkit.getPlayer(p.getUUID()).getLocation()) <= Math.pow(range, 2) && p.getUUID() != uuid) {
                nearbyProfiles.add(p);
            }
        }

        return nearbyProfiles;
    }

    public List<Profile> getOnlineProfiles() {
        return profileList.stream().filter(profile -> Bukkit.getPlayer(profile.getUUID()) != null && Bukkit.getPlayer(profile.getUUID()).isOnline()).collect(Collectors.toList());
    }

    public List<Profile> getProfilesInWorld(World world) {
        return profileList.stream().filter(profile -> Bukkit.getPlayer(profile.getUUID()) != null &&  Bukkit.getPlayer(profile.getUUID()).getWorld().equals(world)).collect(Collectors.toList());
    }

    public Profile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    public Profile getProfile(UUID uuid) {
        if (!isUUIDRegistered(uuid)) return null;
        for (Profile profile : profileList) {
            if (profile.getUUID().equals(uuid)) return profile;
        }
        return null;
    }

    public boolean isUUIDRegistered(UUID uuid) {
        if (profileList == null || profileList.isEmpty()) return false;
        return profileList.stream().anyMatch(p -> p.getUUID().equals(uuid));
    }
}
