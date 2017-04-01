package cn.zw.part.five.websocket;

import java.nio.charset.Charset;
import java.util.Date;

import com.thoughtworks.qdox.model.util.OrderedMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class WebSocketServer {
	
	
	public  void  bind(int port){
		
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup workers = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, workers)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					//  把请求解码 或者 响应编码 的消息  处理为 http。
					p.addLast("http-codec",new HttpServerCodec());
					p.addLast("aggregator",new HttpObjectAggregator(65536));
					
					p.addLast("http-chunked",new ChunkedWriteHandler());
					p.addLast(new WebSocketHandler());
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
	
	
	private class WebSocketHandler extends SimpleChannelInboundHandler<Object>{

		
		private WebSocketServerHandshaker handerShaker;
		
		@Override
		protected void messageReceived(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			if(msg instanceof FullHttpRequest){
				
				handlerHttpRequest(ctx,(FullHttpRequest)msg);
				
			}else if ( msg instanceof WebSocketFrame){
				handlerHttpRequest(ctx,(WebSocketFrame)msg);
			}
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
				ctx.flush();
		}
		
		
		/**
		 *  处理 http的请求
		 * @param ctx
		 * @param req
		 */
		private void handlerHttpRequest(ChannelHandlerContext ctx,
				FullHttpRequest req) {
			
			//  如果http解码失败
			if(!req.getDecoderResult().isSuccess()||
					 !"websocket".equals( req.headers().get("Upgrade"))
					){
				sendHttpResponse(ctx,req,
						new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
				return ;
			}
			
			// 构造握手响应返回
			
			WebSocketServerHandshakerFactory wsFactory =
					new WebSocketServerHandshakerFactory("ws://localhost:9000/websocket", null, false);
			
			handerShaker = wsFactory.newHandshaker(req);
			
			if(null==handerShaker){
				WebSocketServerHandshakerFactory
				.sendUnsupportedWebSocketVersionResponse(ctx.channel());
			}else{
				handerShaker.handshake(ctx.channel(), req);
			}
			
		}

		/**
		 *  处理websocket消息
		 * @param ctx
		 * @param msg
		 */
		private void handlerHttpRequest(ChannelHandlerContext ctx,
				WebSocketFrame frame)  throws Exception {
			
			// 判断是否是关闭链接的命令
			if( frame instanceof CloseWebSocketFrame ){
				handerShaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
				return ;
			}
			
			//  判断是否是PING的消息
			if ( frame instanceof  PingWebSocketFrame ){
				ctx.channel()
				.write(new PongWebSocketFrame(frame.content().retain()));
				return ;
			}
			
			//  如果不是文本 消息
			if(  !(frame instanceof TextWebSocketFrame) ){
				throw  new Exception(String.format("%s frame types not supported", frame.getClass().getName()));
			}
			String req =  ((TextWebSocketFrame)frame).text();
			ctx.channel().write(new TextWebSocketFrame(  new Date().toLocaleString() +": " + req  ) );
			
			
		}
		
		private void sendHttpResponse(ChannelHandlerContext ctx,
				FullHttpRequest req,
				DefaultFullHttpResponse res) {
			
			if(res.getStatus().code() != 200 ){
				ByteBuf buffer =  Unpooled.copiedBuffer(res.getStatus().toString(),CharsetUtil.UTF_8);
				res.content().writeBytes(buffer);
				buffer.release();
				setContentLength(res,res.content().readableBytes());
			}
			
			ChannelFuture f =  ctx.channel().writeAndFlush(res);
			
			
		}

		private void setContentLength(DefaultFullHttpResponse res,
				int readableBytes) {
			
		}
		
	}
	
	public static void main(String[] args) {
		
		new WebSocketServer().bind(9000);
		
	}

}
