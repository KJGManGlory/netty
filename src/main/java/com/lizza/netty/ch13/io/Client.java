package com.lizza.netty.ch13.io;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2020-01-30
 */
public class Client {

    public static void main(String[] args) throws Exception{

        // 1. 创建Socket对象
        Socket socket = new Socket("localhost", 8899);

        // 2. 创建输入流，将文件映射到输入流
        FileInputStream inputStream = new FileInputStream("/Users/lizza/Downloads/Compressed/spark-3.0.0-preview2-bin-hadoop2.7.gz");

        // 3. 获取输出流，将输入流中的数据写入到输出流
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[4096];
        int read = 0;
        int total = 0;

        long start = System.currentTimeMillis();

        while ((read = inputStream.read(buffer, 0, buffer.length)) > 0) {
            outputStream.write(buffer);
            total += read;
        }

        System.out.println("total: " + total + ", cost: " + (System.currentTimeMillis() - start));

        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
