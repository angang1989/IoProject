package com.angang.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BioSocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);

        while (true) {
            System.out.println("等待客户端连接");

            Socket socket = serverSocket.accept();

            System.out.println("有客户端连接");

            handler(socket);
        }

    }

     private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备接收数据");
        int read = clientSocket.getInputStream().read(bytes);

         System.out.println("接收数据完毕");

         if(read != -1) {
             System.out.println(String.format("接收客户端的数据:%s", new String(bytes, 0, read)));
         }

         clientSocket.shutdownInput();

         OutputStream os = clientSocket.getOutputStream();

         PrintWriter pw = new PrintWriter(os);
         pw.write("服务器欢迎你");
         pw.flush();

         clientSocket.shutdownOutput();

         pw.close();
         os.close();
         clientSocket.close();
     }
}
