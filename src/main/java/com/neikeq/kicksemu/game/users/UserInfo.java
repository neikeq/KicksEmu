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

    private final int id;

    private final SqlUtils sqlUtils;

    // Sql getters

    public int getId() {
        return id;
    }

    public String getUsername() {
        return sqlUtils.getString("username");
    }

    public String getPassword() {
        return sqlUtils.getString("password");
    }

    public int getKash() {
        return sqlUtils.getInt("kash");
    }

    public UserSettings getSettings() {
        String query = "SELECT settings_camera, settings_names, vol_effects, vol_music," +
                " settings_invites, settings_shadows, settings_whispers, settings_country" +
                " FROM users WHERE id = ?";

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

    public Timestamp getLastCharDeletion() {
        return sqlUtils.getTimestamp("last_char_deletion");
    }

    public int getSlotOne() {
        return sqlUtils.getInt("slot_one");
    }

    public int getSlotTwo() {
        return sqlUtils.getInt("slot_two");
    }

    public int getSlotThree() {
        return sqlUtils.getInt("slot_three");
    }

    // Sql setters

    public boolean setKash(int value) {
        return sqlUtils.setInt("kash", value);
    }

    public boolean setLastCharDeletion(Date value) {
        return sqlUtils.setTimestamp("last_char_deletion", DateUtils.toTimestamp(value));
    }

    public boolean setSlotOne(int value) {
        return sqlUtils.setInt("slot_one", value);
    }

    public boolean setSlotTwo(int value) {
        return sqlUtils.setInt("slot_two", value);
    }

    public boolean setSlotThree(int value) {
        return sqlUtils.setInt("slot_three", value);
    }

    public boolean setOnline(boolean value) {
        return sqlUtils.setBoolean("online", value);
    }

    public boolean setSettings(UserSettings settings) {
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

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    // Method utils

    public boolean hasEmptySlot() {
        return getSlotOne() <= 0 || getSlotTwo() <= 0 || getSlotThree() <= 0;
    }

    public int getFirstEmptySlot() {
        if (getSlotOne() <= 0) {
            return 0;
        } else if (getSlotTwo() <= 0) {
            return 1;
        } else if (getSlotThree() <= 0) {
            return 2;
        } else {
            return -1;
        }
    }

    public int characterSlot(int characterId) {
        if (getSlotOne() == characterId) {
            return 0;
        } else if (getSlotTwo() == characterId) {
            return 1;
        } else if (getSlotThree() == characterId) {
            return 2;
        } else {
            return -1;
        }
    }

    public boolean hasCharacter(int characterId) {
        return characterSlot(characterId) >= 0;
    }

    public void setSlotWithIndex(int index, int value) {
        switch (index) {
            case 0:
                setSlotOne(value);
                break;
            case 1:
                setSlotTwo(value);
                break;
            case 2:
                setSlotThree(value);
                break;
            default:
        }
    }

    public UserInfo(int id) {
        this.id = id;
        this.sqlUtils = new SqlUtils(id, "users");
    }
}
