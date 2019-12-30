package com.learner.netty.ch8;

import java.nio.ByteBuffer;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2019-12-29
 */
public class NIO_2 {

    public static void main(String[] args){
        ByteBuffer buffer_1 = ByteBuffer.allocate(512);
        ByteBuffer buffer_2 = ByteBuffer.allocateDirect(512);
        buffer_1.put("你好！".getBytes());
        buffer_2.put("你好！".getBytes());
        buffer_1.flip();
        buffer_2.flip();
        System.out.println(new String(buffer_1.array(), 0, buffer_1.limit()));

        // buffer_2.array()为何会报异常？
        System.out.println(new String(buffer_2.array(), 0, buffer_1.limit()));
    }
}
