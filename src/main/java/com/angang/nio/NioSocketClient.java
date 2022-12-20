package com.angang.nio;

import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class NioSocketClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8889);

        OutputStream os = socket.getOutputStream();

        PrintWriter pw = new PrintWriter(os);
        Random random = new Random();
        int i = random.nextInt(100);
        pw.write("我是客户端" + i);
        pw.flush();

        socket.shutdownOutput();

//        InputStream is = socket.getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//        String info = br.readLine();
//
//        if(StrUtil.isNotBlank(info)) {
//            System.out.println(String.format("服务端返回信息：%s", info));
////            System.out.println("服务端返回信息:" + info);
//        }


//        br.close();
//        is.close();
        os.close();
        pw.close();
        socket.close();
    }
}
