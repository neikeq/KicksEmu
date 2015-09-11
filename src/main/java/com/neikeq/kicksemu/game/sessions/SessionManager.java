package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.game.misc.Moderation;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.Password;
import com.neikeq.kicksemu.utils.RandomGenerator;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SessionManager {

    private static final AttributeKey<Session> SESSIONS_KEY = AttributeKey.valueOf("sessions");

    public static void handleConnection(Channel channel) {
        if (!Moderation.isRemoteAddressBanned((InetSocketAddress) channel.remoteAddress())) {
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

        String hash = "";

        try {
            byte[] salt = RandomGenerator.randomBytes(24);
            byte[] addressHash = Password.hashAddress(session.getRemoteAddress(), salt);
            hash = Password.toBase64(salt) + "$" + Password.toBase64(addressHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Output.println("Exception when generating session hash: " +
                    e.getMessage(), Level.DEBUG);
        }

        SessionInfo.insertSession(sessionId, session.getUserId(), session.getPlayerId(), hash);
        session.setSessionId(sessionId);
    }
}
