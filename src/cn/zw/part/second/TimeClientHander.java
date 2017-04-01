package cn.zw.part.second;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHander extends ChannelHandlerAdapter {
	
	private ByteBuf firstMessage;
	
	private  int  count = 0 ;
	byte[] req = null;
	
	public TimeClientHander() {
		req =  ( "QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
		
	
	}
	
	//  当客户端 和 服务器 建立 成功之后 会进入这个方法，发送查询时间的的指令给服务器。调用 ChannelHandlerContext.writeAndFlush method !
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for (int i = 0; i < 100; i++) {
			firstMessage = Unpooled.buffer(req.length);
			firstMessage.writeBytes(req);
			ctx.writeAndFlush(firstMessage);
		}
	}
	
	//  当服务器返回应答的时候 ，就会进入这个方法
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
			/*ByteBuf buf = (ByteBuf)msg;
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req);
			String body  = new String(req);
			System.out.println("client: now:"+body + "[" + ++count +"]");*/
			String body = (String)msg;
			count++;
			System.out.println("client: now:"+body + "[" + count +"]");
		
	}
	
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	}

}
