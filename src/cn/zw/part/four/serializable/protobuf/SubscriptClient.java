package cn.zw.part.four.serializable.protobuf;

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
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class SubscriptClient {
	
	
	public  void  connetion(int port,String host){
		
		EventLoopGroup boss = new NioEventLoopGroup();
		
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(boss).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p =  ch.pipeline();
					p.addLast(new ProtobufVarint32FrameDecoder());
					p.addLast(new ProtobufDecoder(SubscriptResp.subscriptResp.getDefaultInstance()));
					
					p.addLast(new ProtobufVarint32LengthFieldPrepender());
					p.addLast(new ProtobufEncoder());
					p.addLast(new SubscriptClientHandler());
					
				}
			});
			
			ChannelFuture f =  b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			boss.shutdownGracefully();
		}
		
	}
	
	private class SubscriptClientHandler extends ChannelHandlerAdapter{
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for (int i = 1; i <=  10; i++) {
				ctx.write(req(i));
			}
			ctx.flush();
			
		}
		
		
		public  SubscriptReq.subscriptReq req(int i){
			 SubscriptReq.subscriptReq req = SubscriptReq.subscriptReq.newBuilder()
					.setAddress("address")
					.setPhoneNumber(" phone ")
					.setProductName("product")
					.setUserName(" user ")
					.setSubReqID(i).build();
			return req;
		}
		
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			System.out.println("----------------");
			System.out.println("　　　 recevied msg : " + msg);
			
		}
		
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
			ctx.flush();
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
