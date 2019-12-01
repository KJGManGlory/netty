package com.learner.netty.ch2.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-11-29
 */
public class ChannelHandle extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("请求地址：" + ctx.channel().remoteAddress() + ", 请求消息：" + msg);
        ctx.writeAndFlush("From server: " + msg + ", " + UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
