package cn.zw.part.four.serializable.byjava;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SubscriptClient {
	
	
	public  void  connetion(int port ,String host){
		EventLoopGroup boss = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(boss).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p =  ch.pipeline();
					p.addLast(new ObjectDecoder(1024, 
							ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					p.addLast(new ObjectEncoder());
					p.addLast(new SubscriptClientHandler());
				}
			});
			ChannelFuture f  =  b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			boss.shutdownGracefully();
		}
	}
	
	private class SubscriptClientHandler extends ChannelHandlerAdapter{
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for (int i = 1; i <= 10; i++) {
				ctx.write(req(i));
			}
			ctx.flush();
		}
		
		public  SubscriptReq req(int id){
			SubscriptReq req = new SubscriptReq();
			req.setSubReqID(id);
			req.setAddress("呵呵，填写地址栏");
			req.setPhoneNumber("18215658699");
			req.setProductName("the name of  product");
			req.setUserName(" the  name  of  user");
			return req;
		}
		
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			SubscriptResp resp = (SubscriptResp)msg;
			System.out.println(" recevied  the msg : "+resp.toString());
			
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
		
	}
	
	public static void main(String[] args) {
		
		 new SubscriptClient().connetion(9000, "127.0.0.1");
		
	}

}
