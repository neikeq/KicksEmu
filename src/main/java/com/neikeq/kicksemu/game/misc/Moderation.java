package com.neikeq.kicksemu.game.misc;

import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.utils.DateUtils;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Moderation {

    public static boolean isRemoteAddressBanned(InetSocketAddress address) {
        final String query = "SELECT id FROM blacklist WHERE remote_address = ? AND expire > ? LIMIT 1";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, address.getAddress().getHostAddress());
            stmt.setTimestamp(2, DateUtils.getTimestamp());
            
            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            return true;
        }
    }

    public static boolean isUserBanned(int userId) {
        final String query = "SELECT id FROM bans WHERE user_id = ? AND expire > ? LIMIT 1";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, DateUtils.getTimestamp());

            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }

        } catch (SQLException e) {
            return true;
        }
    }
}
