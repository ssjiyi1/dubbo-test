package cn.zw.part.four.serializable.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubscriptServer {
	
	public void  bind(int  port){
		
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup workers = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, workers)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p = 	ch.pipeline();
					// 半包处理
					p.addLast(new ProtobufVarint32FrameDecoder());
					//  解码的目标类型
					p.addLast(new ProtobufDecoder(SubscriptReq.subscriptReq.getDefaultInstance()));
					
					p.addLast(new ProtobufVarint32LengthFieldPrepender());
					p.addLast(new ProtobufEncoder());
					
					p.addLast(new SubscriptServerHandler());
				}
			});
			
			ChannelFuture f =  b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			boss.shutdownGracefully();
			workers.shutdownGracefully();
		}
	}
	
	private class SubscriptServerHandler extends ChannelHandlerAdapter{
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
		SubscriptReq.subscriptReq req = (SubscriptReq.subscriptReq)msg;
		System.out.println(" server accept msg: " + req.toString());
			ctx.writeAndFlush(resp(req));
		}
		
		public  SubscriptResp.subscriptResp resp(SubscriptReq.subscriptReq  req){
			SubscriptResp.subscriptResp resp = SubscriptResp.subscriptResp.newBuilder()
					.setSubReqId(req.getSubReqID()).setRespCode(200).setDesc("he,successed ").build();
			return  resp;
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
				cause.printStackTrace();
				ctx.close();
		}
		
	}
	
	public static void main(String[] args) {
		
		new SubscriptServer().bind(9000);
	}

}
