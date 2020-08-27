package com.lizza.netty.ch5.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-09
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        // 以块的形式去写入处理
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合http请求，聚合成FullHttpRequest，FullHTTPResponse：netty是以分段或分块的形式去通信
        // 所以处理器每次只能接收处理其中的一段；9048为参数，以字节为单位
        pipeline.addLast(new HttpObjectAggregator(9048));
        // WebSocketServerProtocolHandler简化websocket开发
        // 参数类似于项目名称，对于webSocket来讲，是以ws开头的：ws://ip:port/project_name
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new ServerHandle());
    }
}
