package com.nowcoder.toutiao.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/16 16:04
 */
public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    private String data;//新鲜事数据，代表数据关系，a存什么b什么
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }
    //当vo.userName使用,velocity模板自动解析、调用不同key方法
    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }

}
