package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.ClubManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerUtils {

    public static void serverList(Session session, ClientMessage msg) {
        List<Short> servers = getServerList(msg.readShort());

        byte result = (byte)(servers == null ? 255 : 0);

        ServerMessage response = MessageBuilder.serverList(servers, result);
        session.send(response);
    }

    public static void serverInfo(Session session, ClientMessage msg) {
        short serverId = msg.readShort();
        short level = msg.readShort();

        byte result = 0;

        int playerId = session.getPlayerId();

        /* TODO Check if character can access private server... Reject code is 251 */
        if (level == PlayerInfo.getLevel(playerId)) {
            int clubId = PlayerInfo.getClubId(playerId);

            if (ServerInfo.getType(serverId) == GameServerType.CLUB &&
                    (clubId <= 0 || !ClubManager.clubExist(clubId))) {
                result = (byte)252; // Need to be a club member
            } else if (level < ServerInfo.getMinLevel(serverId) ||
                    level > ServerInfo.getMaxLevel(serverId)) {
                result = (byte)253; // Level not allowed
            } else if (ServerInfo.getMaxUsers(serverId) <=
                    ServerInfo.getConnectedUsers(serverId)) {
                result = (byte)254; // Server is full
            }
        } else {
            result = (byte)255; // System problem
        }

        ServerMessage response = MessageBuilder.serverInfo(serverId, result);
        session.send(response);
    }

    private static List<Short> getServerList(short filter) {
        List<Short> servers = new ArrayList<>();
        String query = "SELECT id FROM servers WHERE filter = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, filter);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servers.add(rs.getShort("id"));
                }

                return servers;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean serverExist(short id) {
        String query = "SELECT 1 FROM servers WHERE id = ? LIMIT 1";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean insertServer(ServerBase base) {
        String query = "INSERT INTO servers (id, filter, name, address, port, min_level," +
                " max_level, max_users, type," +
                " exp_factor, point_factor, kash_factor, practice_rewards)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, base.getId());
            stmt.setShort(2, base.getFilter());
            stmt.setString(3, base.getName());
            stmt.setString(4, base.getAddress());
            stmt.setShort(5, base.getPort());
            stmt.setByte(6, base.getMinLevel());
            stmt.setByte(7, base.getMaxLevel());
            stmt.setShort(8, base.getMaxUsers());
            stmt.setString(9, base.getType().name());
            stmt.setInt(10, base.getExpFactor());
            stmt.setInt(11, base.getPointFactor());
            stmt.setInt(12, base.getKashFactor());
            stmt.setBoolean(13, base.isPracticeRewards());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean updateServer(ServerBase base) {
        String query = "UPDATE servers SET filter=?, name=?, address=?, port=?, min_level=?," +
                " max_level=?, max_users=?, connected_users=?, online=?, type=?, exp_factor=?," +
                " point_factor=?, kash_factor=?, practice_rewards=? WHERE id=?";


        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, base.getFilter());
            stmt.setString(2, base.getName());
            stmt.setString(3, base.getAddress());
            stmt.setShort(4, base.getPort());
            stmt.setByte(5, base.getMinLevel());
            stmt.setByte(6, base.getMaxLevel());
            stmt.setShort(7, base.getMaxUsers());
            stmt.setShort(8, (short)0);
            stmt.setBoolean(9, true);
            stmt.setString(10, base.getType().name());
            stmt.setInt(11, base.getExpFactor());
            stmt.setInt(12, base.getPointFactor());
            stmt.setInt(13, base.getKashFactor());
            stmt.setBoolean(14, base.isPracticeRewards());
            stmt.setShort(15, base.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // TODO Add functionality to this method
    public static void nextTip(Session session) {
        String tip = "";

        ServerMessage response = MessageBuilder.nextTip(tip, (byte)0);
        session.send(response);
    }
}
