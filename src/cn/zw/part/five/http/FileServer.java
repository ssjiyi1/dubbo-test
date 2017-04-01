package cn.zw.part.five.http;

import java.io.File;
import java.io.RandomAccessFile;

import javax.xml.transform.OutputKeys;

import org.apache.tools.ant.taskdefs.SendEmail;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class FileServer {
	
	
	private String  root;
	
	public FileServer(String root ) {
		this.root = root;
	}
	
	 public  void  bind(int port){

		 EventLoopGroup boss = new NioEventLoopGroup();
		 
		 EventLoopGroup workers = new NioEventLoopGroup();
		 
		 try {
			ServerBootstrap b = new ServerBootstrap();
			 b.group(boss, workers)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 1024)
			 .childHandler(new LoggingHandler(LogLevel.INFO))
			 .handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p =  ch.pipeline();
					// 请求消息的解码器
					p.addLast("http-decoder",new HttpRequestDecoder());
					// 把多个消息转换为单一的FullHttpRequest 或者 FullHttpResponse (Http解码器 在每个HTTP消息中会生产多个消息对象)
					p.addLast("http-aggregator",new HttpObjectAggregator(65536));
					// 添加响应编码器 
					p.addLast("http-encoder",new HttpResponseEncoder());
					
					// 支持异步发送大的码流，不至于内存溢出。
					p.addLast("http-chunked",new ChunkedWriteHandler());
					
					p.addLast(new FileServerHandler(root));
					
					
					
				}
			});
			 
			ChannelFuture f =  b.bind(port).sync();
			System.out.println("文件服务器启动成功 ...");
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  finally{
			boss.shutdownGracefully();
			workers.shutdownGracefully();
		}
	 }
	 
	private class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

		private String url;
		
		public FileServerHandler(String url) {
			this.url = url;
		}
		
		@Override
		protected void messageReceived(ChannelHandlerContext ch,
				FullHttpRequest req) throws Exception {
			
			if(!req.getDecoderResult().isSuccess()){
				
				
			}
			
			if( !req.getMethod().name().equals("GET")){
				
			}
			
			
			final String uri = req.getUri();
			final String path = sanitizeUri(uri);
			
			if(path==null){
				
			}
			
			
			File file = new File(path);
			
			if( file.isHidden()  || !file.exists() ){
				
				
			}
			
			if(file.isDirectory()){
				
				
				return ;
			}
			
			
			if(!file.isFile()){
				
				
				
			}
			
			RandomAccessFile r = new RandomAccessFile(file, "r");
			long filelength = r.length();
			
			HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			
			
			
			
			
			
			
			
			
			
		}
		
		
		private String sanitizeUri(String uri){
			return "";
			
		}
		
		
	}
	 

}
