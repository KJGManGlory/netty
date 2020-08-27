package com.lizza.netty.ch9;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @Desc: FileLock
 * @author: lizza1643@gmail.com
 * @date: 2019-12-28
 */
public class NIO_2 {

    public static void main(String[] args) throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("nio_9.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        FileLock fileLock = fileChannel.lock(0, 1, false);
        System.out.println(fileLock.isValid());
    }
}
