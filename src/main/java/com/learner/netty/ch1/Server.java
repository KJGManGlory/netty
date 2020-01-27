package com.learner.netty.ch1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-11-28
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        // boosGroup线程组负责获取连接，转接给workGroup，workGroup处理连接
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // ServerBootstrap用于简化启动netty服务端
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 将boosGroup和workGroup进行绑定，利用反射创建通道，绑定子处理器
            serverBootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)  // 反射创建实例
                    .childHandler(new ServerInitializer());   // 子处理器，自己的请求处理服务器，用于处理请求

            // 绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            System.out.println(">>>>>>>> 接收服务已启动，端口：8899");
            channelFuture.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
