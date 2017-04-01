package cn.zw.part.three.delimiter;

import org.apache.tools.ant.taskdefs.UpToDate;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {
	
	 public  void  connction(int port ,String host){
		 
		 EventLoopGroup boss = new NioEventLoopGroup();
		 try {
		 Bootstrap b = new Bootstrap();
		 b.group(boss).channel(NioSocketChannel.class)
		 .option(ChannelOption.TCP_NODELAY, true)
		 .handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ByteBuf delimiter =  Unpooled.copiedBuffer("$_".getBytes());
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new EchoClientHandler());
			}
		});
			ChannelFuture f =  b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			boss.shutdownGracefully();
		}
		 
	 }
	 
	private class EchoClientHandler extends ChannelHandlerAdapter{
		
		private final String ECHO_REQ = "HI ,ZHANGWEI ! \r\n  \\s hh   $_";
		
		private int  count = 0 ; // 计数器
		
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("---");
			for (int i = 0; i < 10; i++) {
				ByteBuf req =  Unpooled.copiedBuffer(ECHO_REQ.getBytes());
				ctx.writeAndFlush(req);
			}
			
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			String resp = (String)msg;
			System.out.println("this is  "+  ++count +" recived the  msg : "+ resp);
		}
		 
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
				cause.printStackTrace();
				ctx.close();
		}
		
	 }
	
	public static void main(String[] args) {
		
		new EchoClient().connction(9000, "127.0.0.1");
		
	}
	 

}
