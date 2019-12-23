java.io
java.nio

java.io中最为核心的概念是流(stream)，面向流的编程。程序中是通过流来读写数据。io中的流只能是输入或者输出流中的一种
java.nio中的三个核心概念：selector，channel，buffer；NIO中是面向block或者buffer来编程的

关于NIO中Buffer的三个重要属性的含义：position，limit，capacity

Java NIO中Buffer: 是一个放置元素的容器
    capacity: buffer的容量，始终为正数，始终大于0
    limit: buffer中不可被读或者被写的第一个元素的索引；初始化时，limit与capacity相同；在
    使用过程中buffer读入元素时并不会改变limit的值，只有在写出时，调用flip才会改变limit的值；
    整个过程中，limit在读写元素时，表示的是元素的个数，而capacity表示的是容量
    position: buffer中将要被读或者被写的下一个元素的索引
```
package com.learner.netty.ch7;

import java.nio.IntBuffer;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-21
 */
public class NIO_4 {

    public static void main(String[] args){
        IntBuffer buffer = IntBuffer.allocate(10);
        // capacity: 10, limit: 10, position: 0
        System.out.println("capacity: " + buffer.capacity()
            + ", limit: " + buffer.limit()
            + ", position: " + buffer.position());
        // 1. 读入4个元素
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        buffer.put(4);
        // capacity: 10, limit: 10, position: 4
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());
        // 写出元素
        buffer.flip();
        // capacity: 10, limit: 4, position: 0
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());
        // 写出所有元素
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
        // capacity: 10, limit: 4, position: 4
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());
        buffer.flip();
        // capacity: 10, limit: 4, position: 0
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        buffer.put(4);
        // 不能读入的元素
//        buffer.put(5);

        // 调用clear，重置所有
        buffer.clear();
        // capacity: 10, limit: 10, position: 0
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        buffer.put(4);
        // 不能写入的元素
        buffer.put(5);
        // capacity: 10, limit: 10, position: 5
        System.out.println("capacity: " + buffer.capacity()
                + ", limit: " + buffer.limit()
                + ", position: " + buffer.position());


    }
}
```
- allocate()方法：初始化buffer，position初始化为0，capacity为指定大小，limit等于
capacity，mark为-1，底层是一个数组

- flip()方法：翻转buffer，将limit设置为当前的position值，将position设置为0
```
public final Buffer flip() {
    limit = position;
    position = 0;
    mark = -1;
    return this;
}
```
- clear()方法：将limit设置为capacity，将position设置为0

```
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
```