package cn.zw.part.new2.heatbeat.dto;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:45.
 */
public class NettyMessage {

    private Header header;

    private String data;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", data='" + data + '\'' +
                '}';
    }

    public NettyMessage(MsgType msgType) {
        Header header= new Header();
        header.setMainVersion(1);
        header.setSubVersion(1);
        header.setLength(10);
        header.setType(msgType.getType());
        this.header = header;
        this.data = "ping";
    }

    public NettyMessage() {
    }
}
