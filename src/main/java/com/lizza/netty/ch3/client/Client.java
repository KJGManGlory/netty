package com.lizza.netty.ch3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-07
 */
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 1. 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 2. 创建BootStrap
            Bootstrap bootstrap = new Bootstrap();

            // 3. 注册initializer，handle
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            // 4. 获取channel
            Channel channel = bootstrap.connect("localhost", 8899).sync().channel();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for(;;) {
                channel.writeAndFlush( reader.readLine() + "\r\n");
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
