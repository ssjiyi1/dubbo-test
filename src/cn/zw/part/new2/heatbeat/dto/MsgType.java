package cn.zw.part.new2.heatbeat.dto;

/**
 * Created by Administrator
 * on 2016/7/14
 * 10:24.
 */
public enum MsgType {

    BEAT(1),BUSINESS(2);
    private int type;

    MsgType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
