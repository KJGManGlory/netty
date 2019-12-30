package com.learner.netty.ch7;

import java.nio.IntBuffer;

/**
 * @Desc: slice buffer 和 buffer; slice buffer 是 buffer 的子序列; 与buffer共享相同的数据
 *
 * @author: lizza1643@gmail.com
 * @date: 2019-12-23
 */
public class NIO_6 {

    public static void main(String[] args){
        IntBuffer buffer = IntBuffer.allocate(10);

        for (int i = 0; i < 10; i++) {
            buffer.put(i);
        }

        buffer.clear();
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }


        buffer.position(3).limit(8);
        IntBuffer slice_buffer = buffer.slice();
        for (int i = 0; i < slice_buffer.capacity(); i++) {
            slice_buffer.put(i, slice_buffer.get(i) * 2);
        }

        buffer.position(0).limit(buffer.capacity());

        System.out.println("--------------");
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }

    }
}
