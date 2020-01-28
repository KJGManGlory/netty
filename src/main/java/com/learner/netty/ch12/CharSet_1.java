package com.learner.netty.ch12;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @Desc: Java 字符集
 * 将charset_1中内容复制到charset_2.txt中
 * @author: lizza1643@gmail.com
 * @date: 2020-01-27
 */
public class CharSet_1 {

    public static void main(String[] args) throws Exception {

        // 1. 创建源文件和目标文件的RandomAccessFile
        RandomAccessFile sourceFile = new RandomAccessFile("charset_1.txt", "r");
        RandomAccessFile targetFile = new RandomAccessFile("charset_2.txt", "rw");

        // 2. 创建源文件和目标文件FileChannel
        FileChannel inputChannel = sourceFile.getChannel();
        FileChannel outputChannel = targetFile.getChannel();

        // 3. 创建内存映射对象MappedByteBuffer
        MappedByteBuffer mappedByteBuffer = inputChannel.map(
                FileChannel.MapMode.READ_ONLY, 0, sourceFile.length());

        // 4. 编码，创建CharSet对象，创建编解码对象
        Charset charset = Charset.forName("utf-8");
        CharsetEncoder encoder = charset.newEncoder();
        CharsetDecoder decoder = charset.newDecoder();

        // 5. 获取解码后的CharBuffer对象，然后编码成ByteBuffer对象
        CharBuffer charBuffer = decoder.decode(mappedByteBuffer);
        ByteBuffer byteBuffer = encoder.encode(charBuffer);

        // 6. 写到目标文件中
        outputChannel.write(byteBuffer);

        // 7. 流关闭
        inputChannel.close();
        outputChannel.close();

        System.out.println("======================");

        Charset.availableCharsets().forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });

        System.out.println("======================");
    }
}
