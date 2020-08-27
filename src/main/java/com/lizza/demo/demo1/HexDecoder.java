package com.lizza.demo.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

/**
 * 字节码的解析器
 */
public class HexDecoder extends StringDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readableBytes();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes);
        System.out.println(">>> 数据解码: " + msg);
        out.add(new String(bytes));
    }
}
