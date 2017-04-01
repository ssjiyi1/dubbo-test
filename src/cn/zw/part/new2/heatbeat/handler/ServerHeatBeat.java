package cn.zw.part.new2.heatbeat.handler;

import cn.zw.part.new2.heatbeat.dto.MsgType;
import cn.zw.part.new2.heatbeat.dto.NettyMessage;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by Administrator
 * on 2016/7/14
 * 10:15.
 */
@ChannelHandler.Sharable
public class ServerHeatBeat extends ChannelHandlerAdapter {
    private int writeBeatFailAmount  = 0;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if(IdleState.WRITER_IDLE.equals(e.state())){ //
                NettyMessage nettyMessage  =  new NettyMessage(MsgType.BEAT);
                ChannelFuture channelFuture  = ctx.writeAndFlush(nettyMessage);
                channelFuture.addListener(new WriteIdleListener());
            }
        }
    }
    class WriteIdleListener implements ChannelFutureListener{
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(!future.isSuccess()){
                writeBeatFailAmount++;
                System.out.println("服务器准备发送心跳，心跳不成功,次数："+writeBeatFailAmount +future.channel());
                if(writeBeatFailAmount>=3){
                    writeBeatFailAmount = 0;
                    System.out.println("关闭连接--->"+future.channel());
                    future.channel().close();
                }
            }else{
//                System.out.println(future.channel() + "for sharable :" + (testSharable++)  );
            }
        }
    }


}
