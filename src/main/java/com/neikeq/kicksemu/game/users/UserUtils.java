package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserUtils {

    public static boolean isAlreadyConnected(int userId) {
        String query = "SELECT online FROM users WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet result = stmt.executeQuery()) {
                return result.next() && result.getShort("online") >= 0;
            }
        } catch (SQLException e) {
            return true;
        }
    }

    public static int getIdFromUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    public static boolean accountExist(int userId) {
        String query = "SELECT id FROM users WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            return false;
        }
    }
}
