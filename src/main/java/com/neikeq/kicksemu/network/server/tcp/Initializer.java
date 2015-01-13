package com.neikeq.kicksemu.network.server.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

class Initializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel c) throws Exception {
        c.pipeline().addLast(new IdleStateHandler(30, 0, 0));
        c.pipeline().addLast(new ClientIdleHandler());
        c.pipeline().addLast(new Decoder());
        c.pipeline().addLast(new ClientHandler());
    }
}
