package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.storage.SqlUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClubInfo {

    private static final String TABLE = "clubs";

    private static final int MEMBERSHIP_LIMIT = 30;

    public static String getName(int id, ConnectionRef ... con) {
        return SqlUtils.getString("name", TABLE, id, con);
    }

    public static int getClubPoints(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("club_points", TABLE, id, con);
    }

    public static boolean isUniformActive(int id, ConnectionRef ... con) {
        return SqlUtils.getBoolean("uniform_active", TABLE, id, con);
    }

    public static int getManager(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "SELECT id FROM club_members " +
                    "WHERE club_id = ? AND role = ? LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "MANAGER");

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt("id") : -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static List<Integer> getCaptains(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "SELECT id FROM club_members " +
                    "WHERE club_id = ? AND role = ? LIMIT 2";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "CAPTAIN");

                try (ResultSet rs = stmt.executeQuery()) {
                    List<Integer> captains = new ArrayList<>();

                    while (rs.next()) {
                        captains.add(rs.getInt("id"));
                    }

                    return captains;
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static List<Integer> getMembers(int id, int offset, int limit, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "SELECT id FROM club_members " +
                    "WHERE club_id = ? AND role NOT IN(?, ?) LIMIT ? OFFSET ?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "PENDING");
                stmt.setString(3, "REJECTED");
                stmt.setInt(4, limit);
                stmt.setInt(5, offset);

                try (ResultSet rs = stmt.executeQuery()) {
                    List<Integer> members = new ArrayList<>();

                    while(rs.next()) {
                        members.add(rs.getInt("id"));
                    }

                    return members;
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static short getMembersCount(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {

            final String query = "SELECT count(1) FROM club_members " +
                    "WHERE club_id = ? AND role NOT IN(?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, "PENDING");
                stmt.setString(3, "REJECTED");

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getShort(1) : -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static short getMembersLimit(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "SELECT extra_membership FROM clubs WHERE id = ? LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        short membersLimit = MEMBERSHIP_LIMIT;

                        if (rs.getBoolean("extra_membership")) {
                            membersLimit += 10;
                        }

                        return membersLimit;
                    } else {
                        return MEMBERSHIP_LIMIT;
                    }
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static ClubUniform getUniform(int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "SELECT uniform_home_shirts, uniform_home_pants, " +
                    "uniform_home_socks, uniform_home_wrist, uniform_away_shirts, " +
                    "uniform_away_pants, uniform_away_socks, uniform_away_wrist " +
                    "FROM " + TABLE + " WHERE id = ? LIMIT 1;";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? new ClubUniform(
                            rs.getInt("uniform_home_shirts"), rs.getInt("uniform_home_pants"),
                            rs.getInt("uniform_home_socks"), rs.getInt("uniform_home_wrist"),
                            rs.getInt("uniform_away_shirts"), rs.getInt("uniform_away_pants"),
                            rs.getInt("uniform_away_socks"), rs.getInt("uniform_away_wrist")) : new ClubUniform();
                }
            }
        } catch (SQLException e) {
            return new ClubUniform();
        }
    }

    public static void setName(String value, int id, ConnectionRef ... con) {
        SqlUtils.setString("name", value, TABLE, id, con);
    }

    public static void sumClubPoints(int value, int id, ConnectionRef ... con) {
        SqlUtils.sumInt("club_points", value, TABLE, id, con);
    }

    public static void setUniformActive(boolean value, int id, ConnectionRef ... con) {
        SqlUtils.setBoolean("uniform_active", value, TABLE, id, con);
    }

    public static void setHomeUniform(Uniform uniform, int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "UPDATE " + TABLE + " SET uniform_home_shirts = ?, " +
                    "uniform_home_pants = ?, uniform_home_socks = ?, " +
                    "uniform_home_wrist = ? WHERE id = ? LIMIT 1;";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, uniform.getShirts());
                stmt.setInt(2, uniform.getPants());
                stmt.setInt(3, uniform.getSocks());
                stmt.setInt(4, uniform.getWrist());
                stmt.setInt(5, id);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void setAwayUniform(Uniform uniform, int id, ConnectionRef ... con) {
        try (ConnectionRef connection = ConnectionRef.ref()) {
            final String query = "UPDATE " + TABLE + " SET uniform_away_shirts = ?, " +
                    "uniform_away_pants = ?, uniform_away_socks = ?, " +
                    "uniform_away_wrist = ? WHERE id = ? LIMIT 1;";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, uniform.getShirts());
                stmt.setInt(2, uniform.getPants());
                stmt.setInt(3, uniform.getSocks());
                stmt.setInt(4, uniform.getWrist());
                stmt.setInt(5, id);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }
}
