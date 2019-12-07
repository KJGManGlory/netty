package com.learner.netty.ch1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Desc: 初始化管道
 * @author: lizza1643@gmail.com
 * @date: 2019-11-28
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // pipeline是一个管道，其中有很多的channelHandle（相当于拦截器），用于处理不同的事务
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("serverCodec", new HttpServerCodec());
        pipeline.addLast("channelHandle", new ServerHandle());
    }
}
