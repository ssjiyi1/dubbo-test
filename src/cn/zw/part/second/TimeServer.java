package cn.zw.part.second;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {
	
	
	public  void  bind(int port){
		EventLoopGroup boss = new NioEventLoopGroup(); // 用于保存新建的连接
		EventLoopGroup works = new NioEventLoopGroup(); //用于已建立连接的socket通讯
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, works).channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChildChannelHandler());
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			boss.shutdownGracefully();
			works.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<Channel>{
		@Override
		protected void initChannel(Channel ch) throws Exception {
			System.out.println("server initChannel ...");
			/**
			 *  工作原理：
			 *  	依次遍历ByteBuf里面的可读字节，判断是否有\n或者\r\n 。如果有就至此结束
			 *     
			 *      从可读所用到结束，就组成了一行数据。它是以换行符结束标记的解码器。支持携带结束符或者不携带结束符两种解码方式
			 *      
			 *      同时支持配置当行的最大长度。如果连接读取到最大长度后仍然没有发现换行符。就会抛出异常。同时忽略之前读的数据
			 *  
			 */
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			/**
			 *    把接受的字符转换为字符串。然后继续调用后面的handle。它和  LineBasedFrameDecoder 组合 就成为了 按行切换文本的解码器，
			 *    
			 *    它们被用来解决tcp的粘包和拆包
			 */
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	public static void main(String[] args) {
		new TimeServer().bind(9000);
	}
	 

}
