package com.lizza.netty.ch3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-04
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        // 1. 创建两个线程组
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 2. 创建ServerBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer())
                    .bind(8899)
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 3. 关闭
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
