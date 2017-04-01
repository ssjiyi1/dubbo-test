package cn.zw.part.new2.protocol.client;

import cn.zw.part.new2.protocol.dto.Header;
import cn.zw.part.new2.protocol.dto.NettyMessage;
import cn.zw.part.new2.protocol.handler.MessageDecode;
import cn.zw.part.new2.protocol.handler.MessageEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.Charset;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:58.
 */
public class MainClient {


    static class ClientServer extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            byte[] buffer = new byte[msg.readableBytes()];
            msg.readBytes(buffer);
            System.out.println("客户端收到消息：" + new String(buffer, Charset.forName("UTF-8")));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 1; i < 100; i++) {
                NettyMessage nettyMessage = new NettyMessage();
                nettyMessage.setData("你好,现在是第：" + i + "次");
                Header header = new Header();
                header.setType(i);
                header.setLength(20);
                header.setMainVersion(1);
                header.setSubVersion(1);
                nettyMessage.setHeader(header);
                ctx.writeAndFlush(nettyMessage);
            }
        }
    }

    public static void main(String[] args) {
        EventLoopGroup workers = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workers)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            /**
                             *
                             * 1024: 消息的最大长度
                             *  0： 长度的起始偏移量
                             *  4： 长度属性占的字节数
                             *  0： 长度调节值，在总长被定义为包含包头长度时，修正信息长度
                             *  4： 跳过的字节数，根据需要我们跳过前4个字节，以便接收端直接接受到不含“长度属性
                             *
                             */
                            channelPipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            channelPipeline.addLast(new MessageDecode());
                            channelPipeline.addLast(new MessageEncode());
                            channelPipeline.addLast(new ClientServer());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 9999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workers.shutdownGracefully();
        }

    }

}
