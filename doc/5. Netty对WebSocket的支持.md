## 一、概述
Netty提供了对WebSocket的支持，利用Netty相关的组件，可以快速的开发WebSocket程序

## 二、示例
利用Netty的相关组件，开发一个WebSocket服务；开发一个页面，作为客户端，与服务进行通信

### 1. 服务端
##### 1.1 Server
```
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
```
##### 1.2 ServerInitializer
```
package com.learner.netty.ch5.server;

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
```
##### 1.3 ServerHandle
```
package com.learner.netty.ch5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.time.LocalDateTime;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-09
 */
public class ServerHandle extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("接收到的信息: " + msg.text());

        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded: " + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved: " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```
### 2. 客户端
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
<form onsubmit="return false;">
    <textarea name="a1" id="a1" style="width: 300px; height: 150px;"></textarea>
    <button onclick="send(this.form.a1.value)">发送数据</button>
    <h3>服务端内容</h3>
    <textarea id="a2" style="width: 300px; height: 150px;"></textarea>
    <button onclick="javascript: document.getElementById('a2').value = '';">清空数据</button>
</form>
<script>
    var socket = null;
    if (window.WebSocket) {
        socket = new WebSocket("ws://localhost:8899/ws")

        socket.onmessage = function (ev) {
            var obj = document.getElementById("a2");
            obj.value = obj.value + "\n" + ev.data;
        }

        socket.onopen = function (ev) {
            var obj = document.getElementById("a2");
            obj.value = "连接开启!";
        }

        socket.onclose = function (ev) {
            var obj = document.getElementById("a2");
            obj.value = obj.value + "\n" + "连接关闭！";
        }

    } else {
        alert("浏览器不支持websocket")
    }

    function send(msg) {
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState == WebSocket.OPEN) {
            socket.send(msg)
        } else {
            alert("连接尚未开启!");
        }
    }
</script>
</body>
</html>
```
### 3. 运行
启动服务后，在浏览器打开网页
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019120914090967.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0tKR01hbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191209141003270.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0tKR01hbg==,size_16,color_FFFFFF,t_70)
输入信息，点击发送数据，可以看到服务端和客户端的数据回显
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191209141141903.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0tKR01hbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191209141121907.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0tKR01hbg==,size_16,color_FFFFFF,t_70)

## 三、其他
- HttpObjectAggregator：是一个http对象聚合器，将http请求和响应聚合成FullHttpRequest，FullHttpResponse
- WebSocketServerProtocolHandle：用于简化websocket开发；
- Frame：对于websocket来讲，数据是以frame来进行传递的；比如textFrame表示文本frame，pingFrame表示心跳frame
- ServerHandle：channelRead0的writeAndFlush需要构建
- channelId：有一个长的ID（全局唯一），和一个短的ID（全局不唯一）
- 在浏览器中可以看到，建立的是http请求，但是升级成了webSocket
![image](http://note.youdao.com/yws/public/resource/5b1dfde86756432c5abd697502aaedc0/935310C832A842BDB57D2ACEA3C7FC7A?ynotemdtimestamp=1575863553271)
