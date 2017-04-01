package cn.zw.part.new2.heatbeat.handler;

import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import cn.zw.part.new2.heatbeat.dto.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Administrator
 * on 2016/7/13
 * 15:44.
 */
public class MessageEncode extends MessageToByteEncoder<NettyMessage> {

//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("---------Server MessageEncode add---------");
//        ctx.channel().close();
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("---------Server handlerRemoved add---------");
//    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out) throws Exception {
        ByteBuf sendBuffer = Unpooled.buffer(); //
        final Header header = msg.getHeader();
        sendBuffer.writeInt(header.getLength());
        sendBuffer.writeInt(header.getType());
        sendBuffer.writeInt(header.getMainVersion());
        sendBuffer.writeInt(header.getSubVersion());
        sendBuffer.writeInt(msg.getData().getBytes().length);
        sendBuffer.writeBytes(msg.getData().getBytes());
        out.writeInt(sendBuffer.readableBytes());
        out.writeBytes(sendBuffer);
        ctx.flush();
    }


}
