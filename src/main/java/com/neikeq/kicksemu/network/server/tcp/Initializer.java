package com.neikeq.kicksemu.network.server.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

class Initializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel c) throws Exception {
        c.pipeline().addLast(new Decoder());
        c.pipeline().addLast(new ClientHandler());
    }
}
