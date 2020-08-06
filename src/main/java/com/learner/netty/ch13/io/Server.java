package com.learner.netty.ch13.io;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Desc:
 * @author: lizza1643@gmail.com
 * @date: 2020-01-30
 */
public class Server {

    public static void main(String[] args) throws Exception{
        // 1. 创建ServerSocket对象
        ServerSocket serverSocket = new ServerSocket(8899);

        while (true) {
            // 2. 获取Socket对象
            Socket socket = serverSocket.accept();

            // 3. 创建DataInputStream
            DataInputStream inputStream = new DataInputStream(
                    socket.getInputStream());

            byte[] buffer = new byte[4096];

            // 4. 读取数据
            while (true){
                int read = inputStream.read(buffer, 0, buffer.length);

                if (read == -1) break;
            }

        }

    }
}
