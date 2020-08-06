package com.learner.netty.ch13.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2020-01-31
 */
public class Server {

    public static void main(String[] args) throws Exception{

        // 1. 创建ServerSocketChannel对象
        ServerSocketChannel serverSocketChannel =
                ServerSocketChannel.open();

        // 2. 获取ServerSocket对象，并配置端口，
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(8899));

        while (true) {
            // 3. 监听端口
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);

            // 4. 读取数据
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int read = 0;
            while (read != -1) {
                read = socketChannel.read(buffer);
                // 将buffer执行rewind操作，否则position的值在最后
                buffer.rewind();
            }

        }


    }
}
