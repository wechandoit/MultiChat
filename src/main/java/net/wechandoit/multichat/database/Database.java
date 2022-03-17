package net.wechandoit.multichat.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;


public abstract class Database {
    JavaPlugin plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "profiles";

    public Database(JavaPlugin instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
            ResultSet rs = ps.executeQuery();

            close(ps, rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public String getChatGroup(UUID uuid) {
        return getChatGroup(uuid.toString());
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public String getChatGroup(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + uuid + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) { // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return rs.getString("chatGroup"); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "global";
    }

    public Boolean hasProfanityFilter(UUID uuid) {
        return hasProfanityFilter(uuid.toString());
    }

    // Exact same method here, Except as mentioned above i am looking for total!
    public Boolean hasProfanityFilter(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + uuid + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) {
                    return rs.getBoolean("hasProfanityFilter");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    // Now we need methods to save things to the database
    public void setTokens(UUID uuid, String chatGroup, boolean hasProfanityFilter) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid,chatGroup,hasProfanityFilter) VALUES(?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.setString(1, uuid.toString());                                             // YOU MUST put these into this line!! And depending on how many
            // colums you put (say you made 5) All 5 need to be in the brackets
            // Seperated with comma's (,) AND there needs to be the same amount of
            // question marks in the VALUES brackets. Right now i only have 3 colums
            // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)

            ps.setString(2, chatGroup); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
            // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
            // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
            // Tokens from another plugin :/
            ps.setBoolean(3, hasProfanityFilter);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}