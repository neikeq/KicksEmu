package com.neikeq.kicksemu.storage;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SqlUtils {

    public static void repeatSetInt(PreparedStatement stmt, Integer ... values) throws SQLException {
        int count = 1;

        for (Integer value : values) {
            stmt.setInt(count, value);
            count++;
        }
    }

    public static void repeatSetInt(PreparedStatement stmt, int startIndex,
                                    Integer ... values) throws SQLException {
        int count = startIndex;

        for (Integer value : values) {
            stmt.setInt(count, value);
            count++;
        }
    }

    public static short getShort(String column, String table, int id, Connection ... con) {
        final String query = "SELECT " + column + " FROM " + table + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getShort(column) : -1;
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

    public static int getInt(String column, String table, int id, Connection ... con) {
        final String query = "SELECT " + column + " FROM " + table + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(column) : -1;
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

    public static boolean getBoolean(String column, String table, int id, Connection ... con) {
        final String query = "SELECT " + column + " FROM " + table + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getBoolean(column);
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getString(String column, String table, int id, Connection ... con) {
        final String query = "SELECT " + column + " FROM " + table + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getString(column) : "";
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

    public static Timestamp getTimestamp(String column, String table, int id,
                                         Connection ... con) {
        final String query = "SELECT " + column + " FROM " + table + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getTimestamp(column) : null;
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static void setShort(String column, short value, String table, int id,
                                Connection ... con) {
        final String query = "UPDATE " + table + " SET " + column + " = ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setShort(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void setInt(String column, int value, String table, int id,
                              Connection ... con) {
        final String query = "UPDATE " + table + " SET " + column + " = ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void setBoolean(String column, boolean value, String table, int id,
                                  Connection ... con) {
        final String query = "UPDATE " + table + " SET " + column + " = ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setBoolean(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void setString(String column, String value, String table, int id,
                                 Connection ... con) {
        final String query = "UPDATE " + table + " SET " + column + " = ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void setTimestamp(String column, Timestamp value, String table, int id,
                                    Connection ... con) {
        final String query = "UPDATE " + table + " SET " + column + " = ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setTimestamp(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void sumShort(String column, short value, String table, int id,
                                Connection ... con) {
        if (value == 0) return;

        final String query = "UPDATE " + table + " SET " + column + " = " + column + " + ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setShort(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }

    public static void sumInt(String column, int value, String table, int id,
                              Connection ... con) {
        if (value == 0) return;

        final String query = "UPDATE " + table + " SET " + column + " = " + column + " + ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, value);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println(e.getMessage(), Level.DEBUG);
        }
    }
}
