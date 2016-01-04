package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserUtils {

    public static boolean isAlreadyConnected(int userId) {
        final String query = "SELECT online FROM users WHERE id = ?";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet result = stmt.executeQuery()) {
                return result.next() && (result.getShort("online") >= 0);
            }
        } catch (SQLException e) {
            return true;
        }
    }

    public static int getIdFromUsername(String username) {
        final String query = "SELECT id FROM users WHERE username = ?";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }

        } catch (SQLException e) {
            return -1;
        }
    }
}
