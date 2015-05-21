package com.neikeq.kicksemu.network.server.udp;

import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.in.handle.MessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.UndefinedMessageException;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.channel.socket.DatagramPacket;

class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ClientMessage message = new ClientMessage(packet.content());

        int playerId = message.readInt(2);

        Session session = ServerManager.getSessionById(playerId);

        if (session.getRemoteAddress().getAddress().equals(packet.sender().getAddress())) {
            int port = packet.sender().getPort();

            // If the client's udp port changed
            if (port != session.getUdpPort()) {
                session.setUdpPort(port);

                // If player is in a room
                if (session.getRoomId() > 0) {
                    Room room = RoomManager.getRoomById(session.getRoomId());

                    // If room is playing, broadcast an update with the new port
                    if (room != null && room.isPlaying()) {
                        if (room.getHost() == playerId) {
                            room.sendBroadcast(MessageBuilder.hostInfo(room));
                        }
                        // TODO else { update player's port }
                    }
                }
            }

            try {
                // Handle the incoming message
                MessageHandler messageHandler = ServerManager.getMessageHandler();
                messageHandler.handle(session, message);

            } catch (UndefinedMessageException ume) {
                Output.println(ume.getMessage() + " from: " +
                        packet.sender().getAddress().getHostAddress(), Level.DEBUG);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Output.println("Udp client exception: " + cause.getMessage(),
                Level.DEBUG);
    }
}
