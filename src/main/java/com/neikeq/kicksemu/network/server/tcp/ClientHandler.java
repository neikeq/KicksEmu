package com.neikeq.kicksemu.network.server.tcp;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.game.sessions.SessionManager;

import com.neikeq.kicksemu.network.packets.in.MessageHandler;
import com.neikeq.kicksemu.network.packets.in.UndefinedMessageException;
import com.neikeq.kicksemu.network.server.ServerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;

        try {
            ClientMessage message = new ClientMessage(buf);

            // Handle the incoming message
            MessageHandler messageHandler = ServerManager.getMessageHandler();
            messageHandler.handle(SessionManager.getSession(ctx.channel()), message);

        } catch (UndefinedMessageException ume) {
            Output.println(ume.getMessage() + " from: " +
                    ctx.channel().remoteAddress().toString(), Level.DEBUG);
        } finally {
            buf.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        SessionManager.getSession(ctx.channel()).flush();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SessionManager.handleConnection(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionManager.removeSession(ctx.channel());
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionManager.removeSession(ctx.channel());

        Output.println("Tcp client exception: " + cause.getMessage(), Level.DEBUG);
    }
}
