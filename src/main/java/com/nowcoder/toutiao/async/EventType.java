package com.nowcoder.toutiao.async;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 10:22
 */
public enum  EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);
    private int value;
    EventType(int value){this.value=value;}
    public int getValue(){return value;}

}
