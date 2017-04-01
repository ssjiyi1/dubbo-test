package cn.zw.part.four.serializable.byjava;

import java.io.Serializable;

public class SubscriptReq  implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private Integer subReqID;
	
	private String userName;
	
	private String productName;
	
	private String phoneNumber;
	
	private String address;

	public Integer getSubReqID() {
		return subReqID;
	}

	public void setSubReqID(Integer subReqID) {
		this.subReqID = subReqID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "SubscriptReq [userName=" + userName + ", productName="
				+ productName + ", phoneNumber=" + phoneNumber + ", address="
				+ address + "]";
	}
}
