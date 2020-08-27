package com.lizza.netty.ch4.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-08
 */
public class ServerHandle extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            System.out.println(ctx.channel().remoteAddress() + "空闲事件：" + state);
            ctx.channel().closeFuture();
        }
    }

}
