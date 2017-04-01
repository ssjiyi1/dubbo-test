package cn.zw.part.first;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

	
	public  void  connection(int port,String host) throws Exception{
		
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			// 当创建NioSocketChannel成功之后，在初始化它的ChannelHander设置到ChannelPieLine中，用于处理网络IO事件
			.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					System.out.println("initChannel ...");
					ch.pipeline().addLast(new TimeClientHander());
				}
			});
			ChannelFuture f =  b.connect(host,port).sync(); // 调用connect发去异步连接，然后调用同步方法，等待连接成功
			f.channel().closeFuture().sync(); // 当客户端关闭以后，首先释放NIO线程组的资源。main在退出
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) {
		int port = 9000;
		try {
			new TimeClient().connection(port, "127.0.0.1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
