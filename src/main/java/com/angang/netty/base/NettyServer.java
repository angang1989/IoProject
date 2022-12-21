package com.angang.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws Exception {
        // 创建两个线程组 bossGroup和workerGroup 子线程NioEventLoop的个数默认为cpu核数的两倍、
        // bossGroup用来从处理链接请求 真正的业务处理由workerGroup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            // 创建服务器的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 配置参数
            bootstrap.group(bossGroup, workerGroup) // 设置连个线程组
                    // 使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 初始化服务器连接队列大小 服务器处理客户端请求是顺序处理的 同一时间只能处理一个客户端连接
                    // 同时有多个连接请求过来 服务端将不能处理的请求 放到队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 对workerGroup的socketChannel设置处理器
                            channel.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture cf = bootstrap.bind(9000).sync();

            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
