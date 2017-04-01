package cn.zw.part.new2.protocol.server;

import cn.zw.part.new2.protocol.dto.NettyMessage;
import cn.zw.part.new2.protocol.handler.MessageDecode;
import cn.zw.part.new2.protocol.handler.MessageEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:40.
 */
public class MainServer {

    static class ServerHandler extends SimpleChannelInboundHandler<NettyMessage> {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
            System.out.println(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, workers)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline cpl = ch.pipeline();
                            cpl.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            cpl.addLast(new MessageDecode());
                            cpl.addLast(new MessageEncode());
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
