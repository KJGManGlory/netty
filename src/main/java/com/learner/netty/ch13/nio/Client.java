package com.learner.netty.ch13.nio;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2020-01-31
 */
public class Client {

    public static void main(String[] args) throws Exception{
        // 1. 创建SocketChannel，连接到服务器，配置非阻塞
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8899));
        socketChannel.configureBlocking(true);

        // 2. 创建FileChannel
        FileChannel fileChannel =
                new FileInputStream("/Users/lizza/Downloads/Compressed/spark-3.0.0-preview2-bin-hadoop2.7.gz")
                        .getChannel();

        long start = System.currentTimeMillis();

        // 3. 将数据传递到SocketChannel
        // transferTo()本身基于零拷贝的原理去实现的
        long total = fileChannel.transferTo(0,fileChannel.size(), socketChannel);

        System.out.println("total: " + total + ", cost: " + (System.currentTimeMillis() - start));

        fileChannel.close();
        socketChannel.close();
    }
}
