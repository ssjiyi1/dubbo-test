package cn.zw.part.new2.heatbeat.server;

import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import cn.zw.part.new2.heatbeat.handler.LogHandler;
import cn.zw.part.new2.heatbeat.handler.MessageDecode;
import cn.zw.part.new2.heatbeat.handler.MessageEncode;
import cn.zw.part.new2.heatbeat.handler.ServerHeatBeat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:40.
 */
public class MainServer {

    static class ServerHandler extends SimpleChannelInboundHandler<NettyMessage> {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
            System.out.println("服务器收到消息:" + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() + "离线");
            ctx.close();
        }


        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelRegistered");
        }


        

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive");
        }
    }

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();
        final LogHandler logHandler = new LogHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, workers)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOW_HALF_CLOSURE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline cpl = ch.pipeline();
                            cpl.addLast(new IdleStateHandler(0, 10, 0));
                            cpl.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            cpl.addLast(new MessageDecode());
                            cpl.addLast(new MessageEncode());
                            cpl.addLast(logHandler);
                            cpl.addLast(new ServerHeatBeat());
                            cpl.addLast(new ServerHandler());

                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind("localhost", 9999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }
}
