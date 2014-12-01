package com.neikeq.kicksemu.network.server.tcp;

import com.neikeq.kicksemu.config.Location;
import com.neikeq.kicksemu.io.Output;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyTcpServer {

    private final int port;
    
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final ServerBootstrap bootstrap;

    private ChannelFuture channelFuture;
    
    public NettyTcpServer(int port) {
        this.port = port;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        initBootstrap();
    }

    public void initBootstrap() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer())
                .option(ChannelOption.SO_BACKLOG, 50)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public void start() throws InterruptedException {
        Output.println(Location.get("net.bind.tcp", String.valueOf(port)));

        channelFuture = bootstrap.bind(port).sync();
    }
    
    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
            channelFuture.awaitUninterruptibly();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
