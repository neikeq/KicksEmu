package com.neikeq.kicksemu.game.misc;

import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class MatchBroadcaster {

    public static void udpGame(Session session, ClientMessage msg) {
        int targetId = msg.getTargetId();

        Room room = RoomManager.getRoomById(session.getRoomId());

        if (room != null && room.isPlayerIn(targetId)) {
            Session targetSession = room.getPlayers().get(targetId);
            Channel ch = KicksEmu.getNettyUdpServer().getChannelFuture().channel();

            try {
                String targetIp = targetSession.getRemoteAddress().getAddress().getHostAddress();

                ch.writeAndFlush(new DatagramPacket(msg.getBody().readerIndex(0).retain(),
                        new InetSocketAddress(targetIp, targetSession.getUdpPort())));
            } finally {
                ch.closeFuture();
            }
        }
    }

    public static boolean isBroadcastEnabled() {
        return Configuration.getBoolean("game.match.broadcast");
    }
}
