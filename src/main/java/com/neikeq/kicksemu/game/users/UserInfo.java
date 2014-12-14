package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserInfo {

    private static final String table = "users";

    // Sql getters

    public static String getUsername(int id) {
        return SqlUtils.getString("username", table, id);
    }

    public static String getPassword(int id) {
        return SqlUtils.getString("password", table, id);
    }

    public static int getKash(int id) {
        return SqlUtils.getInt("kash", table, id);
    }

    public static UserSettings getSettings(int id) {
        String query = "SELECT settings_camera, settings_names, vol_effects, vol_music," +
                " settings_invites, settings_shadows, settings_whispers, settings_country" +
                " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserSettings settings = new UserSettings();

                    settings.setCamera(rs.getByte("settings_camera"));
                    settings.setNames(rs.getByte("settings_names"));
                    settings.setVolEffects(rs.getByte("vol_effects"));
                    settings.setVolMusic(rs.getByte("vol_music"));
                    settings.setInvites(rs.getBoolean("settings_invites"));
                    settings.setShadows(rs.getBoolean("settings_shadows"));
                    settings.setWhispers(rs.getBoolean("settings_whispers"));
                    settings.setCountry(rs.getInt("settings_country"));

                    return settings;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static Timestamp getLastCharDeletion(int id) {
        return SqlUtils.getTimestamp("last_char_deletion", table, id);
    }

    public static int getSlotOne(int id) {
        return SqlUtils.getInt("slot_one", table, id);
    }

    public static int getSlotTwo(int id) {
        return SqlUtils.getInt("slot_two", table, id);
    }

    public static int getSlotThree(int id) {
        return SqlUtils.getInt("slot_three", table, id);
    }

    // Sql setters

    public static void setKash(int value, int id) {
        SqlUtils.setInt("kash", value, table, id);
    }

    public static void setLastCharDeletion(Date value, int id) {
        SqlUtils.setTimestamp("last_char_deletion", DateUtils.toTimestamp(value), table, id);
    }

    public static void setSlotOne(int value, int id) {
        SqlUtils.setInt("slot_one", value, table, id);
    }

    public static void setSlotTwo(int value, int id) {
        SqlUtils.setInt("slot_two", value, table, id);
    }

    public static void setSlotThree(int value, int id) {
        SqlUtils.setInt("slot_three", value, table, id);
    }

    public static void setOnline(boolean value, int id) {
        SqlUtils.setBoolean("online", value, table, id);
    }

    public static void setSettings(UserSettings settings, int id) {
        String query = "UPDATE users SET settings_camera = ?, settings_names = ?," +
                " vol_effects = ?, vol_music = ?, settings_invites = ?," +
                " settings_shadows = ?, settings_whispers = ?, settings_country = ?" +
                " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setByte(1, settings.getCamera());
            stmt.setByte(2, settings.getNames());
            stmt.setByte(3, settings.getVolEffects());
            stmt.setByte(4, settings.getVolMusic());
            stmt.setBoolean(5, settings.getInvites());
            stmt.setBoolean(6, settings.getShadows());
            stmt.setBoolean(7, settings.getWhispers());
            stmt.setInt(8, settings.getCountry());
            stmt.setInt(9, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    // Method utils

    public static boolean hasEmptySlot(int id) {
        return getSlotOne(id) <= 0 || getSlotTwo(id) <= 0 || getSlotThree(id) <= 0;
    }

    public static int getFirstEmptySlot(int id) {
        if (getSlotOne(id) <= 0) {
            return 0;
        } else if (getSlotTwo(id) <= 0) {
            return 1;
        } else if (getSlotThree(id) <= 0) {
            return 2;
        } else {
            return -1;
        }
    }

    public static int characterSlot(int characterId, int id) {
        if (getSlotOne(id) == characterId) {
            return 0;
        } else if (getSlotTwo(id) == characterId) {
            return 1;
        } else if (getSlotThree(id) == characterId) {
            return 2;
        } else {
            return -1;
        }
    }

    public static boolean hasCharacter(int characterId, int id) {
        return characterSlot(characterId, id) >= 0;
    }

    public static void setSlotWithIndex(int index, int value, int id) {
        switch (index) {
            case 0:
                setSlotOne(value, id);
                break;
            case 1:
                setSlotTwo(value, id);
                break;
            case 2:
                setSlotThree(value, id);
                break;
            default:
        }
    }
}
