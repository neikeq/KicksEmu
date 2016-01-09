package com.neikeq.kicksemu.game.misc;

import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class MatchBroadcaster {

    public static void udpGame(Session session, ClientMessage msg) {
        int targetId = msg.getTargetId();

        RoomManager.getRoomById(session.getRoomId()).ifPresent(room -> {
            Session targetSession = room.getPlayer(targetId);

            // If the player is in the room
            if (targetSession != null) {
                Channel ch = KicksEmu.getNettyUdpServer().getChannelFuture().channel();

                try {
                    String targetIp = targetSession.getRemoteAddress().getHostAddress();

                    ch.writeAndFlush(new DatagramPacket(msg.getBody().readerIndex(0).retain(),
                            new InetSocketAddress(targetIp, targetSession.getUdpPort())));
                } finally {
                    ch.closeFuture();
                }
            }
        });
    }

    public static void udpChallengeGame(Session session, ClientMessage msg) {
        int targetId = msg.getTargetId();

        RoomManager.getRoomById(session.getRoomId()).map(r -> (ClubRoom) r).ifPresent(room ->
                ChallengeOrganizer.getChallengeById(room.getChallengeId()).ifPresent(chl -> {
                    Session targetSession = chl.getRoom().getPlayer(targetId);

                    // If the player is in the room
                    if (targetSession != null) {
                        Channel ch = KicksEmu.getNettyUdpServer().getChannelFuture().channel();

                        try {
                            String ip = targetSession.getRemoteAddress().getHostAddress();

                            DatagramPacket packet = new DatagramPacket(
                                    msg.getBody().readerIndex(0).retain(),
                                    new InetSocketAddress(ip, targetSession.getUdpPort()));

                            ch.writeAndFlush(packet);
                        } finally {
                            ch.closeFuture();
                        }
                    }
                })
        );
    }

    public static boolean isBroadcastEnabled() {
        return Configuration.getBoolean("game.match.broadcast");
    }
}
