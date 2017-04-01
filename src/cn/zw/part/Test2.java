package cn.zw.part;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * Created by Administrator
 * on 2016/7/13
 * 11:30.
 */
public class Test2 {
    public static void main(String[] args) {
        String content = "hello，world";
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        byte[] bufferValue = new byte[byteBuffer.remaining()];
        byteBuffer.get(bufferValue);
        System.out.println(new String(bufferValue));

        ByteBuf buffer = Unpooled.buffer(2);

        buffer.writeBytes(new byte[1024 * 1 * 1024 * 5]);


    }
}
