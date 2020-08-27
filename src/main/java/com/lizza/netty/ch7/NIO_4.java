package com.lizza.netty.ch7;

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
