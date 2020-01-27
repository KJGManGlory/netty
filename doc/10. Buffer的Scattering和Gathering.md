Scattering: 分散

Gathering: 聚合

Channel在进行读写操作时可以接收Buffer数组，对于传入的Buffer
数组，Channel根据传入的顺序进行读写，只有当前的buffer数组完全
读写完成（position和limit的值相同）才会进行下一个数据的读写。

利用这个特性，在解析协议的时候会很方便

示例：
```
package com.learner.netty.ch9;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-28
 */
public class NIO_3 {

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
                System.out.println(read);
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
```
利用nc或者telnet命令测试：
```
➜  ~ nc localhost 8899
abcdefgh
```

结果：

```
read: 9
position: 2, limit: 2
position: 3, limit: 3
position: 4, limit: 4
ab
cde
fgh

read: 9, write: 9, msgLength: 9
```