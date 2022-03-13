package com.dylan.mytty;

import com.dylan.mytty.handler.MyChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

/**
 * @author Dylan
 * @Date : 2022/3/13 - 4:24
 * @Description :
 * @Function :
 */
public class MyNettyServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            System.out.println("initChannel ch : " + channel);
                            ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes(StandardCharsets.UTF_8));
                            channel.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(10240, buf))
                                    .addLast(new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast(new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast(new MyChatServerHandler())
                            ;
                        }
                    });
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
