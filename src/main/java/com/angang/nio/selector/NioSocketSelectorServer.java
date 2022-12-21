package com.angang.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioSocketSelectorServer {
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 创建nio ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8889));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 打开selector处理channel 即创建epoll
        // 实现类：EPollSelectorImpl 调用linux底层 epoll_create函数(创建epoll结构体)
        Selector selector = Selector.open();
        // 把serverSocketChannel注册到selector中 让selector对channel感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("nio selector服务端启动成功");

        //优化版本
        while (true) {
            // 阻塞等待需要处理的事件发生
            // 底层调用linux的epoll_ctl函数（监听）
            selector.select();

            // epoll_wait函数（阻塞线程） 获取待处理的事件集合
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();

            while (selectionKeyIterator.hasNext()) {
                SelectionKey key = selectionKeyIterator.next();

                if(key.isAcceptable()) {
                    ServerSocketChannel channelTmp = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channelTmp.accept();
                    socketChannel.configureBlocking(false);

                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if(key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);

                    if(len > 0) {
                        System.out.println("接收到 消息：" + new String(byteBuffer.array()));
                    } else if (len == -1) {
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }

                // 从事件集合中删除本次处理过的key 房子下次selector重复处理
                selectionKeyIterator.remove();
            }
        }
    }
}
