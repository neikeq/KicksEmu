package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.game.misc.BanManager;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class SessionManager {

    private static final AttributeKey<Session> SESSIONS_KEY = AttributeKey.valueOf("sessions");

    public static void handleConnection(Channel channel) {
        if (!BanManager.isRemoteAddressBanned((InetSocketAddress)channel.remoteAddress())) {
            channel.attr(SESSIONS_KEY).set(new Session(channel));
        } else {
            channel.close();
        }
    }
    
    public static void removeSession(Channel channel) {
        Session session = getSession(channel);

        if (session != null) {
            session.close();
        }
    }

    public static Session getSession(Channel channel) {
        return channel.attr(SESSIONS_KEY).get();
    }

    public static synchronized void generateSession(Session session) {
        int sessionId = SessionInfo.generateSessionId();

        SessionInfo.insertSession(sessionId, session.getUserId(), session.getPlayerId());
        session.setSessionId(sessionId);
    }
}
