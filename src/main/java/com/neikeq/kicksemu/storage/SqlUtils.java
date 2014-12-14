package com.neikeq.kicksemu.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SqlUtils {

    public static byte getByte(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getByte(row);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static short getShort(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getShort(row);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static int getInt(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(row);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public static boolean getBoolean(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(row);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getString(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(row);
                } else {
                    return "";
                }
            }
        } catch (SQLException e) {
            return "";
        }
    }

    public static Timestamp getTimestamp(String row, String table, int id) {
        String query = "SELECT " + row + " FROM " + table + " WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp(row);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static void setByte(String row, byte value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setByte(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    public static void setShort(String row, short value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setShort(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    public static void setInt(String row, int value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    public static void setBoolean(String row, boolean value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    public static void setString(String row, String value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }

    public static void setTimestamp(String row, Timestamp value, String table, int id) {
        String query = "UPDATE " + table + " SET " + row + " = ? WHERE id = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, value);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException ignored) {}
    }
}
