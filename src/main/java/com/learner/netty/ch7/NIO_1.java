package com.learner.netty.ch7;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * @Desc: 从文件中读取数据打印到控制台
 * @author: lizza1643@gmail.com
 * @date: 2019-12-16
 */
public class NIO_1 {

    public static void main(String[] args) throws Exception {
        FileInputStream stream = new FileInputStream("nio_1.txt");
        // 获取Channel
        FileChannel channel = stream.getChannel();
        // 创建字节数组，将数据写入
        ByteBuffer buffer = ByteBuffer.allocate(512);
        channel.read(buffer);

        // 翻转缓冲区
        buffer.flip();

        while (buffer.remaining() > 0){
            System.out.println((char)buffer.get());
        }

        stream.close();

    }
}
