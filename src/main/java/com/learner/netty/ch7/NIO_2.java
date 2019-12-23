package com.learner.netty.ch7;

import java.nio.IntBuffer;
import java.security.SecureRandom;

/**
 * @Desc: 从IntBuffer中读取数据打印至控制台
 * @author: lizza1643@gmail.com
 * @date: 2019-12-16
 */
public class NIO_2 {

    public static void main(String[] args){
        IntBuffer buffer = IntBuffer.allocate(10);
        // 将数据写入buffer
        for (int i = 0; i < buffer.capacity(); i++) {
            int random = new SecureRandom().nextInt(20);
            buffer.put(random);
        }

        // 同时进行读取写入时需要调用flip()方法进行翻转？
        buffer.flip();

        // 将buffer中的数据读取出来
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }

    }
}
