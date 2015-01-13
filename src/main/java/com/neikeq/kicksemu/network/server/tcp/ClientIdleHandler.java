package com.neikeq.kicksemu.network.server.tcp;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.sessions.SessionManager;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientIdleHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            if (e.state() == IdleState.READER_IDLE) {
                Session session = SessionManager.getSession(ctx.channel());

                if (session.getPingState() > 0) {
                    session.close();
                } else {
                    session.sendAndFlush(MessageBuilder.tcpPing());
                    session.setPing(session.getPingState() + 1);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Output.println("Idle handler caught an exception: " + cause.getMessage(),
                Level.DEBUG);
    }
}
