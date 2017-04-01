package cn.zw.part.new2.protocol.handler;

import cn.zw.part.new2.protocol.dto.Header;
import cn.zw.part.new2.protocol.dto.NettyMessage;
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
