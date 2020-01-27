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
public class ServerHandle extends SimpleChannelInboundHandler<String> {

    /**
     * 用于获取请求，并进行处理，最后返回响应
     * @author: lizza@vizen.cn
     * @date: 2019/11/28 9:36 下午
     * @param ctx
     * @param msg
     * @return void
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

//        if (msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//
//            // 获取请求类型
//            System.out.println("请求类型："+request.method().name());
//
//            // 获取访问地址
//            System.out.println("请求地址："+request.uri());
//
//            // 1. 构建ByteBuf
//            ByteBuf content = Unpooled.copiedBuffer("Hello, Netty 学习！", CharsetUtil.UTF_8);
//            // 2. 根据ByteBuf构建Response
//            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//                    HttpResponseStatus.OK, content);
//            // 3. 设置响应头
//            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
//            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
//            // 4. 返回
//            ctx.writeAndFlush(response);
//        }

        System.out.println("接收到的数据：" + msg.toString());
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
