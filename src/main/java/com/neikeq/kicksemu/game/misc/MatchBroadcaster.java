package com.neikeq.kicksemu.game.misc;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

public class MatchBroadcaster {

    public static void udpGame(Session session, ClientMessage msg) {
        int targetId = msg.getTargetId();

        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the room exists
        if (room != null) {
            Session targetSession = room.getPlayers().get(targetId);

            // If the player is in the room
            if (targetSession != null) {
                targetSession.getChannel().writeAndFlush(msg.getBody().readerIndex(0).retain());
            }
        }
    }

    public static boolean isBroadcastEnabled() {
        return Configuration.getBoolean("game.match.broadcast");
    }
}
