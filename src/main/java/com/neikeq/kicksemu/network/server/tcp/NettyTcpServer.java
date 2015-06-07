package com.neikeq.kicksemu.network.server.tcp;

import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.io.Output;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.BindException;

public class NettyTcpServer {

    private final int port;
    
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private ChannelFuture channelFuture;
    
    public NettyTcpServer(int port) {
        this.port = port;

        initBootstrap();
    }

    private void initBootstrap() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer())
                .option(ChannelOption.SO_BACKLOG, 50)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public void start() throws InterruptedException, BindException {
        Output.println(Localization.get("net.bind.tcp", String.valueOf(port)));

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
