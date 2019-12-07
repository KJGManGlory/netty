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
