package com.learner.netty.ch10;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-28
 */
public class NIO_1 {

    public static void main(String[] args) throws Exception{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(8899);
        serverSocketChannel.socket().bind(address);

        int msgLength = 2 + 3 + 4;

        ByteBuffer[] buffers = new ByteBuffer[3];
        buffers[0] = ByteBuffer.allocate(2);
        buffers[1] = ByteBuffer.allocate(3);
        buffers[2] = ByteBuffer.allocate(4);

        SocketChannel socketChannel = serverSocketChannel.accept();

        while (true) {
            int read = 0;
            while (read < msgLength) {
                read += socketChannel.read(buffers);
                System.out.println("read: " + read);
                Arrays.asList(buffers).stream()
                        .map(buffer -> "position: " + buffer.position() + ", limit: " + buffer.limit())
                        .forEach(System.out::println);

            }

            Arrays.asList(buffers).forEach(buffer -> {
                buffer.flip();
            });

            int write = 0;
            while (write < msgLength) {
                write += socketChannel.write(buffers);
            }

            Arrays.asList(buffers).forEach(buffer -> {
                buffer.clear();
                // 模拟协议解析
                while (buffer.hasRemaining()){
                    System.out.print((char)buffer.get());
                }
                System.out.println();
                buffer.clear();
            });

            System.out.println("read: " + read + ", write: " + write + ", msgLength: " + msgLength);
        }
    }
}
