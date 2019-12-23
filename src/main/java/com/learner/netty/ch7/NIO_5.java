package com.learner.netty.ch7;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-21
 */
public class NIO_5 {

    public static void main(String[] args) throws Exception{
        FileInputStream is = new FileInputStream("nio_5_is.txt");
        FileOutputStream os = new FileOutputStream("nio_5_os.txt");

        FileChannel isChannel = is.getChannel();
        FileChannel osChannel = os.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(512);

        while (true) {
//            buffer.clear();   // 若将此行注释掉，会出现什么情况？

            /**
             * 若将上面的行注释掉，isChannel.read(buffer)获取到的值为0，所以程序进入死循环
             * 会在文件中不断的输出数据
             */

            int read = isChannel.read(buffer);
            System.out.println("read: " + read);

            if (read == -1) break;

            buffer.flip();
            osChannel.write(buffer);
        }

        os.close();
        is.close();
    }
}
