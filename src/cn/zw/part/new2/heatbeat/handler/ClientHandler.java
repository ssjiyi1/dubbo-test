package cn.zw.part.new2.heatbeat.handler;

import cn.zw.part.new2.heatbeat.client.MainClient;
import cn.zw.part.new2.heatbeat.dto.Header;
import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<NettyMessage> {


        @Override
        protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
            System.out.println("客户端收到消息：" + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("exception");
//            ctx.close();
        }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        ctx.close();
//        System.out.println("重新连接");
//        MainClient.getInstance().doConnection();
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