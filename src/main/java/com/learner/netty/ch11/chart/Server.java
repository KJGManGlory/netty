package com.learner.netty.ch11.chart;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-29
 */
public class Server {

    private static Map<String, SocketChannel> client_map = new HashMap<>();

    public static void main(String[] args) throws Exception{
        // 1. 创建ServerSocketChannel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // 2. 获取ServerSocket对象
        ServerSocket serverSocket = serverSocketChannel.socket();

        // 3. 绑定端口
        serverSocket.bind(new InetSocketAddress(8899));

        // 4. 注册到Selector对象上
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 5. 调用select()方法，监听已注册的channel的事件
            selector.select();

            // 6. 获取SelectionKey，遍历，处理具体事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.forEach(selectionKey -> {
                SocketChannel clientChannel;
                try {
                    if (selectionKey.isAcceptable()) {

                        // 7. 获取对应的ServerSocketChannel，并由此获取客户端的SocketChannel，配置非阻塞
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                        clientChannel = serverChannel.accept();

                        // 8. 将SocketChannel注册到Selector上
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        // 将client保存起来
                        client_map.put("【" + UUID.randomUUID().toString() + "】", clientChannel);

                    } else if (selectionKey.isReadable()) {

                        // 获取客户端SocketChannel
                        clientChannel = (SocketChannel) selectionKey.channel();

                        // 将客户端输入的数据读入ByteBuffer
                        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                        int read = clientChannel.read(buffer);

                        // 打印客户端的数据
                        String msg = null;
                        if (read > 0) {
                            buffer.flip();

                            // 解码
                            Charset charset = Charset.forName("utf-8");
                            msg = String.valueOf(charset.decode(buffer).array());

                            System.out.println("client: " + clientChannel + ", msg: " + msg);
                        }

                        // 遍历client_map获取当前client的key值
                        String key = null;
                        for(Map.Entry<String, SocketChannel> entry : client_map.entrySet()) {
                            if (entry.getValue() == clientChannel) {
                                key = entry.getKey();
                                break;
                            }
                        }

                        // 遍历client_map，将消息发送至其他的client
                        for(Map.Entry<String, SocketChannel> entry : client_map.entrySet()) {
                            buffer.clear();
                            buffer.put((key + ": " + msg).getBytes());
                            buffer.flip();
                            entry.getValue().write(buffer);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // 每次处理完必须清空，否则处理完的事件依然在集合中的，而事件对应channel却不存在了
            selectionKeys.clear();
        }


    }
}
