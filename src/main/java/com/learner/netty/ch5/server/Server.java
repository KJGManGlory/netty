package com.learner.netty.ch5.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-09
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        // 1. 创建EventLoopGroup
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 2. 创建ServerBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 3. 注册handle，绑定端口，设置同步
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer())
                    .bind(8899)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            // 4. 优雅关闭
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
