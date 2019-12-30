package com.learner.netty.ch11;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-28
 */
public class Selector_1 {

    public static void main(String[] args) throws Exception {
        int[] ports = new int[]{50000, 50001, 50002, 50003, 50004};

        Selector selector = Selector.open();

        for (int port : ports) {

            // 1. 获取ServerSocketChannel，并将其设置为非阻塞
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // 2. 获取ServerSocket对象
            ServerSocket serverSocket = serverSocketChannel.socket();

            // 3. 将其绑定到具体的端口
            serverSocket.bind(new InetSocketAddress(port));

            // 4. 将其注册到selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("监听端口：" + port);
        }

        while (true) {
            int number = selector.select();
            System.out.println("number: " + number);

            // 1. 获取SelectionKey的集合
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();

            // 2. 遍历selectionKeySet，获取目标事件的ServerSocketChannel
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                // 接入事件，isAcceptable()对应SelectionKey.OP_ACCEPT；
                // isAcceptable()为true说明有连接进入了
                if (selectionKey.isAcceptable()) {
                    // 3. 获取对应的ServerSocketChannel
                    ServerSocketChannel serverSocketChannel =
                            (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);


                    // 3. 业务处理
                    System.out.println("ServerSocketChannel: " + socketChannel);

                    // 4. 移除SelectionKey.OP_ACCEPT这个key
                    iterator.remove();
                } else if (selectionKey.isReadable()) {
                    // 读取数据
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocateDirect(512);


                    int read = 0;
                    while (true) {

                        // 将客户端的数据读取到Buffer
                        buffer.clear();
                        read = socketChannel.read(buffer);

                        if (read == 0) break;

                        // 将Buffer中的数据写出到控制台
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            System.out.print((char)buffer.get());
                        }

                        // 再次写回客户端
                        buffer.clear();
                        socketChannel.write(buffer);

                    }

                    iterator.remove();
                }
            }
        }


    }
}
