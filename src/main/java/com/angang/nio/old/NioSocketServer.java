package com.angang.nio.old;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NioSocketServer {
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 创建nio ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8889));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        System.out.println("nio原始版本服务端启动成功");

        //初始版本
        //问题1：while循环空转 cpu占用率100%
        //问题2：channelList中的元素每次都全量遍历 优化：1.每次都遍历有数据收发的集合 2.如果没有数据收发 把线程阻塞住
        while (true) {
            // nio的实现是由操作系统内部实现的 底层调用了linux内核的accept函数
            SocketChannel socketChannel = serverSocketChannel.accept();

            if(Objects.nonNull(socketChannel)) {
                System.out.println("客户端连接成功");

                //设置为非阻塞
                socketChannel.configureBlocking(false);

                channelList.add(socketChannel);
            }

            Iterator<SocketChannel> socketChannelIterator = channelList.iterator();

            while (socketChannelIterator.hasNext()) {
                SocketChannel socketChannelTmp = socketChannelIterator.next();

                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                int len = socketChannelTmp.read(byteBuffer);

                if(len > 0) {
                    System.out.println("接收到消息：" + new String(byteBuffer.array()));
                } else if (len == -1) {
                    System.out.println("客户端断开连接");
                    socketChannelIterator.remove();
                }
            }
        }
    }
}
