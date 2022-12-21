package com.angang.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline =  socketChannel.pipeline();
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());

                        pipeline.addLast(new ChatClientHandler());
                    }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

            Channel channel = channelFuture.channel();
            System.out.println(channel.localAddress());

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();

                channel.writeAndFlush(msg);
            }
        } finally {
          group.shutdownGracefully();
        }
    }
}
