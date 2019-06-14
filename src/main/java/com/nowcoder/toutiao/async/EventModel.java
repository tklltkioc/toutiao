package com.nowcoder.toutiao.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 10:30
 */
public class EventModel {
    private EventType type;//点赞还是什么
    private int actorId;//谁评论了
    private int entityType;//评论哪个事件
    private int entityId;//评论哪个事件
    private int entityOwnerId;//关联的用户
    private Map<String ,String>exts=new HashMap<>();

    public EventModel() {

    }//默认函数，反射时首先创建

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }

}
