package com.lizza.demo.demo1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.ServerSocketChannel;

/**
 * 初始化
 */
public class ServerInitializer extends ChannelInitializer<ServerSocketChannel> {

    @Override
    protected void initChannel(ServerSocketChannel ch) throws Exception {
        ch.pipeline().addLast("channelHandle", new ServerHandle());
//        pipeline.addLast("decoder", new HexDecoder());
    }

}
