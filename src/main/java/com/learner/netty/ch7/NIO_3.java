package com.learner.netty.ch7;

import io.netty.buffer.ByteBuf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * @Desc: 向文件中写数据
 * @author: lizza1643@gmail.com
 * @date: 2019-12-19
 */
public class NIO_3 {

    public static void main(String[] args) throws Exception {
        FileOutputStream stream = new FileOutputStream("nio_3.txt");
        FileChannel channel = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(12);
        byte[] arr = "hello world!".getBytes();
        System.out.println(arr.length);
        for (int i = 0; i < arr.length; i++) {
            buffer.put(arr[i]);
        }
        buffer.flip();
        channel.write(buffer);
        stream.close();
    }
}
