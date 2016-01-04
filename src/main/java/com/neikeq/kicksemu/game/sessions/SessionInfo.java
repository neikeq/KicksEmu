package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.RandomGenerator;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionInfo {

    private static final String TABLE = "sessions";

    public static int getPlayerId(int sessionId, ConnectionRef... con) {
        return getInt("player_id", sessionId, con);
    }

    public static void setPlayerId(int playerId, int sessionId, ConnectionRef ... con) {
        SqlUtils.setInt("player_id", playerId, TABLE, sessionId, con);
    }

    public static int getUserId(int sessionId, ConnectionRef ... con) {
        return getInt("user_id", sessionId, con);
    }

    public static String getHash(int sessionId, ConnectionRef ... con) {
        return getString(sessionId, con);
    }

    public static void reduceExpiration(int sessionId) {
        final String query = "UPDATE " + TABLE + " SET expiration = CURRENT_TIMESTAMP + " +
                "INTERVAL 30 SECOND WHERE id = ?";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, sessionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Output.println("Exception when reducing session expiration: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public static void remove(int sessionId) {
        final String query = "DELETE FROM " + TABLE + " WHERE id = ?";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, sessionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Output.println("Exception when removing session: " + e.getMessage(), Level.DEBUG);
        }
    }

    public static void resetExpiration(int sessionId) {
        final String query = "UPDATE " + TABLE + " SET expiration = CURRENT_TIMESTAMP + " +
                "INTERVAL 1 DAY WHERE id = ?";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, sessionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Output.println("Exception when resetting session expiration: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public static int generateSessionId() {
        final String query = "SELECT FLOOR((RAND() * 4294967295) - 2147483648) " +
                "AS random_session_id FROM " + TABLE + " WHERE \"random_session_id\" " +
                "NOT IN (SELECT id FROM " + TABLE + ") LIMIT 1";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("random_session_id") : RandomGenerator.randomInt();
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            return -1;
        }
    }

    public static void insertSession(int sessionId, int userId, int playerId, String hash) {
        final String query = "INSERT INTO " + TABLE + " (id, user_id, player_id, hash, expiration) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP + INTERVAL 1 DAY)";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            stmt.setInt(2, userId);
            stmt.setInt(3, playerId);
            stmt.setString(4, hash);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Output.println("Exception when inserting session to the table: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    private static int getInt(String column, int id, ConnectionRef ... con) {
        final String query = "SELECT " + column + " FROM " + TABLE +
                " WHERE id = ? AND expiration > CURRENT_TIMESTAMP";

        try (ConnectionRef connection = ConnectionRef.ref(con)) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(column) : -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    private static String getString(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref(con)) {
            final String column = "hash";
            final String query = "SELECT " + column + " FROM " + TABLE +
                    " WHERE id = ? AND expiration > CURRENT_TIMESTAMP";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getString(column) : null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }
}
