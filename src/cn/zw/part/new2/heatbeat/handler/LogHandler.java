package cn.zw.part.new2.heatbeat.handler;

import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Administrator
 * on 2016/7/18
 * 11:52.
 */
@ChannelHandler.Sharable
public class LogHandler extends SimpleChannelInboundHandler<NettyMessage> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        System.out.println( this +"-------log-------"+msg);
    }
}
