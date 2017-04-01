package cn.zw.part.new2.heatbeat.client;

import cn.zw.part.new2.heatbeat.dto.MsgType;
import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import cn.zw.part.new2.heatbeat.handler.ClientHandler;
import cn.zw.part.new2.heatbeat.handler.MessageDecode;
import cn.zw.part.new2.heatbeat.handler.MessageEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:58.
 */
public class MainClient {


    private MainClient(){

    }
    private static MainClient mainClient =  new MainClient();

    public static MainClient getInstance() {
        return mainClient;
    }

    private String host = "localhost";
    private int port = 9999;
    private boolean closed = false;
    Channel channel = null;
    Bootstrap bootstrap = null;
    EventLoopGroup workers = null;


    public static void main(String[] args) {
        MainClient mainClient = MainClient.getInstance();
        mainClient.init();
    }
    public  void  closeHistory(){
        closed = true;
        workers.shutdownGracefully();
        System.out.println("停止客户端");
    }
    private void init() {
        if(closed){
            return;
        }
        workers = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workers)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, true)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addLast(new IdleStateHandler(30, 30, 0));
                        channelPipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                        channelPipeline.addLast(new MessageEncode());
                        channelPipeline.addLast(new MessageDecode());
                        channelPipeline.addLast(new ClientHeatBeat());
                        channelPipeline.addLast(new ClientHandler());
                    }
                });
        doConnection();
    }

    public void doConnection() {
        if(closed){
            return;
        }
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ConnectionListenerFuture());
        channel = channelFuture.channel();

    }

    class ConnectionListenerFuture implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                System.out.println("连接服务器成功");
            } else {
                System.out.println("连接服务器失败");
                TimeUnit.SECONDS.sleep(3);
                bootstrap.connect(host, port).addListener(this);
            }

        }
    }



    class ClientHeatBeat extends ChannelHandlerAdapter {
        private int writeBeatFailAmount  = 0;
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if(IdleState.READER_IDLE.equals(e.state())){
                    NettyMessage nettyMessage  = new NettyMessage(MsgType.BEAT);
                    ChannelFuture channelFuture  = ctx.writeAndFlush(nettyMessage);
                    channelFuture.addListener(new WriteIdleListener());
                }
                //  这样判断的弊端：

                /**
                 * 1 : 客户端必须配置 hasf...参数  =  true
                 * 2 : 进入连接后 必须 关闭channel 。不关闭 虽然心跳可以继续。但是
                 *  服务器已经重新启动过了。当前的这个channel其实就是一个废弃的。而且只有关闭了
                 *  才能被后面的handler inactive 结收到
                 *
                 *
                 */


            }else if (evt instanceof ChannelInputShutdownEvent) {
                System.out.println("............ChannelInputShutdownEvent...........断线重线中...");
                TimeUnit.SECONDS.sleep(3);
//                closeHistory();
                ctx.close();
                doConnection();
            }else{
                System.out.println(evt);
            }
        }
        class WriteIdleListener implements ChannelFutureListener{
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    writeBeatFailAmount++;
                    System.out.println("客户端发出心跳失败了 ,次数："+writeBeatFailAmount + future.channel());
                    if(writeBeatFailAmount>=3){
                        writeBeatFailAmount = 0;
                        System.out.println("关闭连接--->"+future.channel());
                        future.channel().close();
                    }
                }
            }
        }


    }

}
