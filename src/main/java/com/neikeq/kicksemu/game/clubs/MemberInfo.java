package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberInfo {

    private static final String TABLE = "club_members";

    public static int getClubId(int id, Connection ... con) {
        return getInt("club_id", TABLE, id, con);
    }

    public static MemberRole getRole(int id, Connection... con) {
        String role = getString("role", TABLE, id, con);
        return role.isEmpty() ? MemberRole.MEMBER : MemberRole.valueOf(role);
    }

    public static int getBackNumber(int id, Connection ... con) {
        return getInt("back_number", TABLE, id, con);
    }

    public static int getInt(String column, String table, int id, Connection ... con) {
        String query = "SELECT " + column + " FROM " + table +
                " WHERE id = ? AND role != 'PENDING' LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(column);
                    } else {
                        return -1;
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static String getString(String column, String table, int id, Connection ... con) {
        String query = "SELECT " + column + " FROM " + table +
                " WHERE id = ? AND role != 'PENDING' LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(column);
                    } else {
                        return "";
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return "";
        }
    }
}
