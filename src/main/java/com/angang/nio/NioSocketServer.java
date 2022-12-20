package com.angang.nio;

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
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8889));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        System.out.println("服务端启动成功");

        while (true) {
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
