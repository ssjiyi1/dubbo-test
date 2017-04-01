import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Test {

	public static void main(String[] args) {


		String decodeStr =  ByteBufUtil.hexDump(Unpooled.copiedBuffer("你好".getBytes()));
		System.out.println(decodeStr);

		DefaultByteBufHolder d = new DefaultByteBufHolder(null);


	}
}
