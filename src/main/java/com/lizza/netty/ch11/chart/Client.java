package com.lizza.netty.ch11.chart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-29
 */
public class Client {

    public static void main(String[] args) throws Exception{

        // 1. 创建SocketChannel对象
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // 2. 连接到服务器
        socketChannel.connect(
                new InetSocketAddress("localhost", 8899));

        // 3. 注册到Selector
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {

            // 4. 选择器阻塞的监听所关注的事件以及注册的Channel
            selector.select();

            // 5. 遍历并处理所关注的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.forEach(selectionKey -> {

                try {
                    if (selectionKey.isConnectable()) {
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

                        // 判断clientChannel是否就绪
                        if (clientChannel.isConnectionPending()) {
                            // ? 真正的建立连接
                            clientChannel.finishConnect();

                            // 向服务端发送链接成功的消息
                            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                            buffer.put((LocalDateTime.now() + "连接成功！").getBytes());
                            buffer.flip();
                            clientChannel.write(buffer);

                            // 接收用户键盘输入信息
                            new Thread(() -> {
                                while (true) {
                                    try {
                                        BufferedReader reader = new BufferedReader(
                                                new InputStreamReader(System.in)
                                        );

                                        String msg = reader.readLine();
                                        buffer.clear();
                                        buffer.put(msg.getBytes());
                                        buffer.flip();
                                        clientChannel.write(buffer);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                        // 注册读事件
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {

                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

                        // 获取服务端发送的数据
                        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                        int read = clientChannel.read(buffer);

                        // 在控制台打印服务端的数据
                        if (read > 0) {
                            String msg = new String(buffer.array(), 0, read);
                            System.out.println(msg);
                        }

                    }
                    selectionKeys.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
