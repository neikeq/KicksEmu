package com.neikeq.kicksemu.network.server.udp;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.utils.DateUtils;

public class UdpPing implements Runnable {

    private static final int SECONDS_LIMIT = 30;

    private final Session session;

    public static void udpPing(Session session) {
        session.setLastPingResponse(DateUtils.currentTimeMillis());
    }

    public static void sendUdpPing(Session session) {
        session.sendAndFlush(MessageBuilder.udpPing());
    }

    @Override
    public void run() {
        long delay = DateUtils.currentTimeMillis() - session.getLastPingResponse();

        if ((delay / 1000) < SECONDS_LIMIT) {
            sendUdpPing(session);
        } else {
            session.close();
        }
    }

    public UdpPing(Session session) {
        this.session = session;
    }
}
