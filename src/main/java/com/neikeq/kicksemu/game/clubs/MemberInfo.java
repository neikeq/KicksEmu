package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberInfo {

    private static final String TABLE = "club_members";

    public static int getClubId(int id, ConnectionRef... con) {
        return getInt("club_id", id, con);
    }

    public static MemberRole getRole(int id, ConnectionRef ... con) {
        String role = getString("role", id, con);
        return role.isEmpty() ? MemberRole.MEMBER : MemberRole.valueOf(role);
    }

    public static int getBackNumber(int id, ConnectionRef ... con) {
        return getInt("back_number", id, con);
    }

    private static int getInt(String column, int id, ConnectionRef ... con) {
        final String query = "SELECT " + column + " FROM " + TABLE +
                " WHERE id = ? AND role NOT IN(?, ?) LIMIT 1;";

        try (ConnectionRef connection = ConnectionRef.ref(con)) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "PENDING");
                stmt.setString(3, "REJECTED");

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(column) : -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    private static String getString(String column, int id, ConnectionRef ... con) {
        final String query = "SELECT " + column + " FROM " + TABLE +
                " WHERE id = ? AND role NOT IN(?, ?) LIMIT 1;";

        try (ConnectionRef connection = ConnectionRef.ref(con)) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "PENDING");
                stmt.setString(3, "REJECTED");

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getString(column) : "";
                }
            }
        } catch (SQLException e) {
            return "";
        }
    }
}
