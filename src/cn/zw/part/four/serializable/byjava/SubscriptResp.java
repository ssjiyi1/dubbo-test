package cn.zw.part.four.serializable.byjava;

import java.io.Serializable;

public class SubscriptResp implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer SubReqID;
	
	private Integer respCode;
	
	private String desc;

	public Integer getSubReqID() {
		return SubReqID;
	}

	public void setSubReqID(Integer subReqID) {
		SubReqID = subReqID;
	}

	public Integer getRespCode() {
		return respCode;
	}

	public void setRespCode(Integer respCode) {
		this.respCode = respCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "SubscriptResp [SubReqID=" + SubReqID + ", respCode=" + respCode
				+ ", desc=" + desc + "]";
	}
	
	
	
}
