package com.neikeq.kicksemu.network.server.udp;

import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.io.Output;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.BindException;

public class NettyUdpServer {

    private final int port;

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final Bootstrap bootstrap = new Bootstrap();

    private ChannelFuture channelFuture;

    public NettyUdpServer(int port) {
        this.port = port;

        initBootstrap();
    }

    private void initBootstrap() {
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ClientHandler());
    }

    public void start() throws InterruptedException, BindException {
        Output.println(Localization.get("net.bind.udp", String.valueOf(port)));

        channelFuture = bootstrap.bind(port).sync();
    }
    
    public void close() {
        if (getChannelFuture() != null) {
            getChannelFuture().channel().close();
            getChannelFuture().awaitUninterruptibly();
        }

        group.shutdownGracefully();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }
}
