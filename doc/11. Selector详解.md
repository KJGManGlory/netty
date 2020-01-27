传统的网络编程，通常服务端会监听指定的端口，当客户端请求
到达时，服务端会新建线程去处理请求；当并发比较低时，这样
的结构不会出现问题，而且编程简单，结构清晰；但是请求比较
多时，这样的架构就会出现问题：过多的线程会占用大量的系统
资源，线程之间的频繁切换也会耗费大量的时间和资源，因此NIO
应用而生；

定义：
`A multiplexor of SelectableChannel objects.`，
一个可选择的多路复用器（暂时不太理解：可选择？多路？复用？）

- selector的创建：
- SelectionKey：包含三个set：key set，selected-key set，cancelled-key set
- key set表示总的事件集合
- selected-key set是key set的子集
- cancelled-key set是key set的子集，表示取消的事件的集合

示例代码
```
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
```
- 编码流程：
    - 服务端：
    1. 创建ServerSocketChannel对象，并配置为非阻塞
    2. 获取ServerSocket对象
    3. 绑定端口
    4. 注册到Selector对象上
    5. 在死循环中，调用select()方法（此方法为阻塞方法），监听已注册的channel的事件
    6. 获取SelectionKey，遍历，处理每一个关注的事件
    7. 获取对应的ServerSocketChannel，并由此获取客户端的SocketChannel，配置非阻塞
    8. 将SocketChannel注册到Selector上
    