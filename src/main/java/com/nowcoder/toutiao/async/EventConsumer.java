package com.nowcoder.toutiao.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.toutiao.util.JedisAdapter;
import com.nowcoder.toutiao.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 10:17
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>>config=new HashMap<>();//所有handler入口
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //查找所有eventhandler实现类，遍历eventhandler关联到的关系events
        Map<String,EventHandler>beans=applicationContext.getBeansOfType(EventHandler.class);
        if (beans!=null){
            for (Map.Entry<String,EventHandler>entry:beans.entrySet()){//循环所有hanlder
                List<EventType>eventTypes=entry.getValue().getSupportEventTypes();

                for (EventType type:eventTypes){
                    if (!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }

            }
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String>events=jedisAdapter.brpop(0,key);//队列无数据则block
                    for (String message:events){
                        if (message.equals(key)){
                            continue;
                        }
                        EventModel eventModel= JSON.parseObject(message,EventModel.class);//字符串转为数据对象
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler handler:config.get(eventModel.getType())){
                            handler.doHandler(eventModel);
                        }

                    }

                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;

    }
}
