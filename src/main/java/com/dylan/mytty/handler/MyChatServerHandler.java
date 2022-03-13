package com.dylan.mytty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Dylan
 * @Date : 2022/3/13 - 4:37
 * @Description :
 * @Function :
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义连接线程组 所有连接到服务端的线程都会在这个线程组中存在 当某一个连接断开时线程组中代表这个断开连接的数据会被移除
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        // 当前会话的channel
        Channel channel = ctx.channel();
        System.out.println("channelGroup.size : " + channelGroup.size());
        System.out.println("接收到消息 : " + s);
        channelGroup.forEach(ch -> {
            if (ch == channel){
                ch.writeAndFlush("【自己】 - " + s + "$_");
            }else {
                ch.writeAndFlush("【ip】: " + ch.remoteAddress() + " - " + s + "$_");
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("【服务器】 - " + ctx.channel().remoteAddress() + " 已加入$_");
        channelGroup.add(ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("【服务器】 - " + ctx.channel().remoteAddress() + " 已离开$_");
        super.handlerRemoved(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String eventType = "";
            switch (idleStateEvent.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            System.out.println(eventType + "_________" + ctx.channel().remoteAddress() + " 这个服务有一段时间没有调用了");
            Channel channel = ctx.channel();
            channel.close();
//            channel.writeAndFlush("你咋了" + "$_");
//            super.userEventTriggered(ctx, evt);
        }
        super.userEventTriggered(ctx, evt);
    }
}
