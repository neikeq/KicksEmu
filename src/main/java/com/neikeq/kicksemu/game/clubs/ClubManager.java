package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClubManager {

    public static void clubInfo(Session session) {
        session.send(MessageBuilder.clubInfo(session.getPlayerId()));
    }

    public static void clubMembers(Session session, ClientMessage msg) {
        byte page = msg.readByte();
        session.send(MessageBuilder.clubMembers(session.getPlayerId(), page));
    }

    public static boolean clubExist(int clubId) {
        String query = "SELECT id FROM clubs WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, clubId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            return false;
        }
    }
}
