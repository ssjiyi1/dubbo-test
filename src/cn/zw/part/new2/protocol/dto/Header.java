package cn.zw.part.new2.protocol.dto;

/**
 * Created by Administrator
 * on 2016/7/13
 * 14:49.
 */
public class Header {

    private int length;
    private int type;
    private int mainVersion;
    private int subVersion;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMainVersion() {
        return mainVersion;
    }

    public void setMainVersion(int mainVersion) {
        this.mainVersion = mainVersion;
    }

    public int getSubVersion() {
        return subVersion;
    }

    public void setSubVersion(int subVersion) {
        this.subVersion = subVersion;
    }

    @Override
    public String toString() {
        return "Header{" +
                "length=" + length +
                ", type=" + type +
                ", mainVersion=" + mainVersion +
                ", subVersion=" + subVersion +
                '}';
    }
}
