package cn.zw.part.four.serializable.protobuf;

import cn.zw.part.four.serializable.protobuf.SubscriptReq.subscriptReq;
import cn.zw.part.four.serializable.protobuf.SubscriptReq.subscriptReq.Builder;

public class Test {
	
	
	public static void main(String[] args) {
		
		Builder b =  SubscriptReq.subscriptReq.newBuilder();
		subscriptReq req =  b.setAddress("地址").setPhoneNumber("电话").setProductName("产品名称").setSubReqID(1).setUserName("用户名称").build();
		
		System.out.println(req.toString());
		
	}

}
