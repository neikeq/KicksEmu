package com.neikeq.kicksemu.network.server.udp;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.in.handle.MessageHandler;
import com.neikeq.kicksemu.network.server.ServerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.channel.socket.DatagramPacket;

class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        try {
            ClientMessage message = new ClientMessage(packet.content());

            int playerId = message.readInt(2);

            MessageHandler messageHandler = ServerManager.getMessageHandler();
            Session session = ServerManager.getSessionById(playerId);

            session.setUdpPort(packet.sender().getPort());

            // Handle the incoming message
            if (!messageHandler.handle(session, message)) {
                Output.println("Received unknown datagram packet (id: " + message.getMessageId() +
                        ") from: " + packet.sender().getAddress().getHostAddress(), Level.DEBUG);
            }
        } finally {
            packet.release();
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
