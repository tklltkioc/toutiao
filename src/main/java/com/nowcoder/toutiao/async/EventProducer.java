package com.nowcoder.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.toutiao.util.JedisAdapter;
import com.nowcoder.toutiao.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 10:43
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json= JSONObject.toJSONString(eventModel);//对象转为字符串
            String key= RedisKeyUtil.getEventQueueKey();//放到redis
            jedisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
