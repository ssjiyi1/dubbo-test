package cn.zw.part.new2.delimiter.lineBaseFrame;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Date;

/**
 * Created by Administrator
 * on 2016/7/12
 * 15:08.
 */
public class TimeServer1 {


    static class ServerAdapter extends ChannelHandlerAdapter {


        public  static  int  count = 0;

        static final String DELIMITER = "￥";


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("----channelReadComplete-count---------:"+count++);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("----channelRead-count---------:"+count++);
            System.out.println("服务器接收到内容:" + msg);
            System.out.println("-------------服务器准备回写客户端内容------------");
            String returnContent = "现在时间:" + new Date().toLocaleString()+DELIMITER;
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer(returnContent.getBytes()));

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();

        }
    }


    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();

        final String DELIMITER = "￥";

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, workers)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline channelPipeline = channel.pipeline();

                            channelPipeline.addLast(new DelimiterBasedFrameDecoder(
                                    1024 * 10 * 10,Unpooled.copiedBuffer(DELIMITER.getBytes())));
                            channelPipeline.addLast(new StringDecoder());

                            channelPipeline.addLast(new ServerAdapter());
                        }
                    });
            try {
               ChannelFuture   channelFuture = bootstrap.bind(9999).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

}
