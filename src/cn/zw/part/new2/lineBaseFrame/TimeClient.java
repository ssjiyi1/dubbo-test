package cn.zw.part.new2.lineBaseFrame;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Administrator
 * on 2016/7/12
 * 15:52.
 */
public class TimeClient {


    static class ClientAdapter extends ChannelHandlerAdapter {

        public static int channelRead = 0;

        public static int channelReadComplete = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("channelRead：" + channelRead++);
            System.out.println("客户端收到消息:"+msg);
//            super.channelRead(ctx, msg);
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String content = "准备发射";
            for(int i = 0;i<100;i++){
                content+=i;
                content += System.getProperty("line.separator");
            }
            ctx.writeAndFlush(Unpooled.copiedBuffer(content.getBytes()));

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelReadComplete：" + channelReadComplete++);
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            final ChannelPipeline channelPipeline = channel.pipeline();
                            //  读取消息，一直读到换行符位置（” \n 或者 \r\n  “）
                            //  如果超过了 1024 个字符都没有发现 换行符 就会抛出异常
                            channelPipeline.addLast(new LineBasedFrameDecoder(1024));
                            channelPipeline.addLast(new StringDecoder());
                            channelPipeline.addLast(new ClientAdapter());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();

        }


    }

}
