package cn.zw.part.three.delimiter;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *   
 *   功能： 服务器接受到客户端的消息
 *   		
 *   		打印出来，然后把原消息返回给客户端
 *   
* @ClassName: EchoServer
* @Description: TODO(这里用一句话描述这个类的作用)
* @author ZhangWei
* @date 2015年7月31日 上午11:42:06
*
 */
public class EchoServer {

	public  void  bind(int port){
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup worksGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, worksGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					
					ByteBuf delimiter =  Unpooled.copiedBuffer("$_".getBytes());
					// 1024 单条消息的最大长度
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoServerHandler());
				}
			});
			ChannelFuture f =  b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			worksGroup.shutdownGracefully();
		}
		
	}
	
	private  class EchoServerHandler extends  ChannelHandlerAdapter{
		int  count = 0;
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			/**
			 *  因为我们自定义了分隔符“$_”。读取数据的时候会忽略这些数据。
			 *  所以在回写客户端的时候，需要在添加上“$_”
			 */
		String body = (String)msg;
		System.out.println("this is  "+ ++count +" times recived msg:"+body);
		body+="$_"	;
		ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
		ctx.writeAndFlush(resp)	;
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
				cause.printStackTrace();
				ctx.close();
		}
	}
	
	
	public static void main(String[] args) {
		new EchoServer().bind(9000);
		
	}
	
}
