package com.angang.netty.chat;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 上线了" + DateUtil.formatDateTime(new DateTime()) + "\n");

        channelGroup.add(channel);

        System.out.println(channel.remoteAddress() + "上线了\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 下线了" + DateUtil.formatDateTime(new DateTime()) + "\n");

        System.out.println(channel.remoteAddress() + "下线了\n");

        System.out.println("channelGroup size = " + channelGroup.size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();

        for (Channel ch : channelGroup) {
            if(channel == ch) {
                ch.writeAndFlush("[自己]发送了消息：" + s + "\n");
            } else {
                ch.writeAndFlush("【客户端】"+ channel.remoteAddress() +"发送了消息：" + s + "\n");
            }
        }
    }
}
