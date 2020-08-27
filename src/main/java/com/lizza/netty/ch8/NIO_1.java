package com.lizza.netty.ch8;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Desc: DirectBuffer 是在JVM 堆外开辟的内存空间
 * 在使用普通的HeapByteBuffer时，是在Java堆上开辟了一块
 * @author: lizza1643@gmail.com
 * @date: 2019-12-24
 */
public class NIO_1 {

    public static void main(String[] args) throws Exception {
        FileInputStream is = new FileInputStream("nio_7_is.txt");
        FileOutputStream os = new FileOutputStream("nio_7_os.txt");

        FileChannel isChannel = is.getChannel();
        FileChannel osChannel = os.getChannel();

        ByteBuffer buffer = ByteBuffer.allocateDirect(512);

        while (isChannel.read(buffer) > 0){
            buffer.flip();
            osChannel.write(buffer);
            buffer.clear();
        }
    }
}
