package com.neikeq.kicksemu.network.server.udp;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.in.handle.MessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.UndefinedMessageException;
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

            if (port != session.getUdpPort()) {
                session.setUdpPort(port);
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
        Output.println("Udp client exception: " + cause.getMessage(), Level.DEBUG);
    }
}
