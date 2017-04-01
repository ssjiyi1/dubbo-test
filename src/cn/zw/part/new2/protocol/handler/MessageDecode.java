package cn.zw.part.new2.protocol.handler;

import cn.zw.part.new2.protocol.dto.Header;
import cn.zw.part.new2.protocol.dto.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Administrator
 * on 2016/7/13
 * 15:54.
 */
public class MessageDecode extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readInt();
        int type = msg.readInt();
        int mainVersion = msg.readInt();
        int subVersion = msg.readInt();
        int dataLength = msg.readInt();
        byte[] data =new byte[dataLength];
        msg.readBytes(data);
        String strData = new String(data, Charset.forName("UTF-8"));
        NettyMessage nettyMessage =  new NettyMessage();
        Header header = new Header();
        nettyMessage.setHeader(header);
        nettyMessage.setData(strData);
        header.setLength(length);
        header.setMainVersion(mainVersion);
        header.setSubVersion(subVersion);
        header.setType(type);
        out.add(nettyMessage);
    }
}
