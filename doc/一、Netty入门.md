## 一、概述
Netty是一个异步的，基于事件驱动的，网络应用框架；

![avatar](https://netty.io/images/components.png)

目前大多使用netty 4，netty 5被废弃了
## 二、大纲
- [ ] Netty入门
- [ ] Netty的Socket编程

## 二、入门示例
### 1. 示例
开发一个简单的服务器，绑定端口8899，服务启动后每次访问都返回helloworld字符串
- Server：入口
```
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
            channelFuture.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
```
- ServerInitializer：服务初始化
```
package com.learner.netty.ch1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.Http2Codec;

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
        pipeline.addLast("channelHandle", new ChannelHandle());
    }
}
```
- ChannelHandle：自定义处理器
```
package com.learner.netty.ch1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @Desc: 自定义的handle，用于处理自己的业务，需要继承SimpleChannelInboundHandler
 * @author: lizza1643@gmail.com
 * @date: 2019-11-28
 */
public class ChannelHandle extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 用于获取请求，并进行处理，最后返回响应
     * @author: lizza@vizen.cn
     * @date: 2019/11/28 9:36 下午
     * @param ctx
     * @param msg
     * @return void
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        System.out.println("请求处理");

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // 获取请求类型
            System.out.println("请求类型："+request.method().name());

            // 获取访问地址
            System.out.println("请求地址："+request.uri());

            // 1. 构建ByteBuf
            ByteBuf content = Unpooled.copiedBuffer("Hello, Netty 学习！", CharsetUtil.UTF_8);
            // 2. 根据ByteBuf构建Response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);
            // 3. 设置响应头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            // 4. 返回
            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("1. handle added");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("2. channel registered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("3. channel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("4. channel inactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("5. channel unregistered");
    }
}
```
- 请求示例
```
curl 'http://localhost:8899'
```
- 返回示例：
```
Hello, Netty 学习！
```
## 三、开发流程
上面的入门示例的开发流程总结如下：

1. 创建两个EventLoopGroup线程组：boos，work
```
// boosGroup线程组负责获取连接，转接给workGroup，workGroup处理连接
EventLoopGroup boosGroup = new NioEventLoopGroup();
EventLoopGroup workGroup = new NioEventLoopGroup();
```
> boos负责获取连接，work负责处理连接

2. 创建ServerBootStrap实例，用于绑定boss和work，创建channel，绑定handle，绑定端口，启动服务，最后优雅的关闭服务
```
// 将boosGroup和workGroup进行绑定，利用反射创建通道，绑定子处理器
serverBootstrap.group(boosGroup, workGroup)
        .channel(NioServerSocketChannel.class)  // 反射创建实例
        .childHandler(new ServerInitializer());   // 子处理器，自己的请求处理服务器，用于处理请求
// 绑定端口
ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
channelFuture.channel().closeFuture().sync();
```
```
boosGroup.shutdownGracefully();
workGroup.shutdownGracefully();
```

3.继承ChannelInitializer创建Initializer，重写initChannel
```
@Override
protected void initChannel(SocketChannel ch) throws Exception {
    // pipeline是一个管道，其中有很多的channelHandle（相当于拦截器），用于处理不同的事务
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast("serverCodec", new HttpServerCodec());
    pipeline.addLast("channelHandle", new ChannelHandle());
}
```
> ChannelPipeline是一个管道，里边可以添加很多的handle，每个handle可以处理不同的事务

4. 继承SimpleChannelInboundHandler，创建自己的ChannelHandle，重写channelRead0()用于处理自己的业务
```
@Override
protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

    System.out.println("请求处理");

    if (msg instanceof HttpRequest) {
        HttpRequest request = (HttpRequest) msg;

        // 获取请求类型
        System.out.println("请求类型："+request.method().name());

        // 获取访问地址
        System.out.println("请求地址："+request.uri());

        // 1. 构建ByteBuf
        ByteBuf content = Unpooled.copiedBuffer("Hello, Netty 学习！", CharsetUtil.UTF_8);
        // 2. 根据ByteBuf构建Response
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, content);
        // 3. 设置响应头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        // 4. 返回
        ctx.writeAndFlush(response);
    }

}
```
## 四、Netty执行流程
### 1. 总览
在自定义的handle中可以重写handlerAdded，channelRegistered，channelActive，channelInactive，channelUnregistered这五个方法，并进行打印，用来分析Netty的执行流程
```
package com.learner.netty.ch1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @Desc: 自定义的handle，用于处理自己的业务，需要继承SimpleChannelInboundHandler
 * @author: lizza1643@gmail.com
 * @date: 2019-11-28
 */
public class ChannelHandle extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 用于获取请求，并进行处理，最后返回响应
     * @author: lizza@vizen.cn
     * @date: 2019/11/28 9:36 下午
     * @param ctx
     * @param msg
     * @return void
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        System.out.println("请求处理");

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // 获取请求类型
            System.out.println("请求类型："+request.method().name());

            // 获取访问地址
            System.out.println("请求地址："+request.uri());

            // 1. 构建ByteBuf
            ByteBuf content = Unpooled.copiedBuffer("Hello, Netty 学习！", CharsetUtil.UTF_8);
            // 2. 根据ByteBuf构建Response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);
            // 3. 设置响应头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            // 4. 返回
            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("1. handle added");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("2. channel registered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("3. channel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("4. channel inactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("5. channel unregistered");
    }
}
```
启动服务，查看执行结果：
```
> Task :Server.main()
1. handle added
2. channel registered
3. channel active
请求处理
请求类型：GET
请求地址：/
请求处理
4. channel inactive
5. channel unregistered
```
可以看到其执行流程是：
1. handle added
2. channel registered
3. channel active
4. 处理业务逻辑
5. channel inactive
6. channel unregistered
