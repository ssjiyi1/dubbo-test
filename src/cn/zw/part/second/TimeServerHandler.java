package cn.zw.part.second;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler  extends ChannelHandlerAdapter{
	
	
	private  int  count ;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		/*ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"utf-8");*/
		String body = (String)msg;
		System.out.println("server: the time  server recive order:"+body + "[" + ++count +" ]" );
		String order = "QUERY TIME ORDER";
		String curTime = body.equalsIgnoreCase(order)?new Date().toLocaleString():"BAD ORDER";
		curTime += System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(curTime.getBytes());
		ctx.writeAndFlush(resp);
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
			ctx.close();
	}
	
}

