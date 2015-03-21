package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;

public class ChatUtils {

    public static void broadcastNotice(String notice) {
        ServerManager.getPlayers().values().stream().forEach(s -> sendServerNotice(s, notice));
    }

    public static void sendServerMessage(Session session, String message) {
        ServerMessage response = MessageBuilder.chatMessage(0, "",
                MessageType.SERVER_MESSAGE, message);
        session.sendAndFlush(response);
    }

    public static void sendServerNotice(Session session, String notice) {
        ServerMessage response = MessageBuilder.chatMessage(0, "",
                MessageType.SERVER_NOTICE, notice);
        session.sendAndFlush(response);
    }
}
