package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.game.chat.MessageType;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ClubManager {

    public static void clubInfo(Session session) {
        int clubId = MemberInfo.getClubId(session.getPlayerId());
        session.send(MessageBuilder.clubInfo(clubId));
    }

    public static void clubMembers(Session session, ClientMessage msg) {
        byte page = msg.readByte();
        session.send(MessageBuilder.clubMembers(session.getPlayerId(), page));
    }

    public static boolean clubExist(int clubId) {
        final String query = "SELECT id FROM clubs WHERE id = ?";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, clubId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public static void onMemberConnectedStateChanged(Session session) {
        int playerId = session.getPlayerId();
        int clubId = MemberInfo.getClubId(playerId);

        boolean disconnected = !session.isAuthenticated();

        List<Integer> members = (clubId > 0) ?
                ClubInfo.getMembers(clubId, 0, 30) : new ArrayList<>();

        members.remove((Integer) playerId);

        if (!members.isEmpty()) {
            String message = disconnected ? " has been disconnected" : " is online";
            ServerMessage notification = MessageBuilder.chatMessage(MessageType.SERVER_MESSAGE,
                    session.getCache().getName() + message);

            try {
                Predicate<Integer> filter = ServerManager::isPlayerConnected;

                if (disconnected) {
                    Room room = RoomManager.getRoomById(session.getRoomId());

                    if (room != null) {
                        filter = filter.and(member -> !room.isPlayerIn(member));
                    }
                }

                members.stream().filter(filter).forEach(member ->
                    ServerManager.getSession(member).sendAndFlush(notification.retain()));
            } finally {
                notification.release();
            }
        }
    }
}
