## 一、概述
netty作为服务端时，可以连接多个客户端。利用此特性，可以开发一个简单的群聊应用。包含以下功能：

- 服务端记录接入
- 上线下线提醒
- 消息群发

功能分析：

- 服务端记录接入： netty的handle中SimpleChannelInboundHandler的channelActive方法可以监听channel是否接入，利用此回调方法来监听客户端是否接入
- 上线下线提醒：handleAdded方法可以监听接入的客户端是否活动，此方法可以监听上线与下线
- 消息群发：channelRead0可以实现服务端和客户端的相互通信，客户端利用channelRead0中的ChannelHandlerContext对象向服务端发送消息，服务端接收到消息后利用ChannelHandlerContext向客户端回写消息；ChannelGroup用于存储当前与服务端连接的客户端channel，遍历该group便可以发送不同的消息给客户端

## 二、代码实现
### 1. 服务端
##### 1.1 主程序
```
package com.learner.netty.ch3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.SocketChannel;

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
```

##### 1.2 ServerInitializer
```
package com.learner.netty.ch3.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-04
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // ★ 分隔符解码器
        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ServerHandle());
    }
}
```
##### 1.3 ServerHandle
```
package com.learner.netty.ch3.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.logging.SocketHandler;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-04
 */
public class ServerHandle extends SimpleChannelInboundHandler<String> {

    /**  **/
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 1. 获取channel
        Channel channel = ctx.channel();
        channels.forEach(ch -> {
            if (ch != channel) {
                ch.writeAndFlush("【" + channel.remoteAddress() + "】：" + msg + "\n");
            } else {
                ch.writeAndFlush("【自己】发送的消息：" + msg + "\n");

            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 1. 获取channel
        Channel channel = ctx.channel();
        // 2. 服务器发送通知
        channels.writeAndFlush("【服务器】 - " + channel.remoteAddress() + " 加入\n");
        // 3. 将新的channel添加至channelGroup
        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 1. 获取channel
        Channel channel = ctx.channel();
        // 2. 服务器发送通知
        channels.writeAndFlush("【服务器】 - " + channel.remoteAddress() + " 离开\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```
##### 1.4 客户端主程序
```
package com.learner.netty.ch3.client;

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
```
##### 1.5 ClientInitializer
```
package com.learner.netty.ch3.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-07
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ClientHandle());
    }
}
```
##### 1.6 ClientHandle
```
package com.learner.netty.ch3.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-07
 */
public class ClientHandle extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }
}
```
启动多个客户端，在控制台输入消息，便可以接收到信息了