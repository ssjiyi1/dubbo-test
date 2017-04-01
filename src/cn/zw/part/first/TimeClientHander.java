package cn.zw.part.first;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHander extends ChannelHandlerAdapter {
	
	private ByteBuf firstMessage;
	
	public TimeClientHander() {
		byte[] req = "QUERY TIME ORDER".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}
	
	//  当客户端 和 服务器 建立 成功之后 会进入这个方法，发送查询时间的的指令给服务器。调用 ChannelHandlerContext.writeAndFlush method !
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(firstMessage);
	}
	
	//  当服务器返回应答的时候 ，就会进入这个方法
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body  = new String(req);
		System.out.println("now:"+body);
	}
	
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("client  read finished ");
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
			cause.printStackTrace();
	}
}
