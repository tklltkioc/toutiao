package com.nowcoder.toutiao.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.toutiao.async.EventHandler;
import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.EntityType;
import com.nowcoder.toutiao.model.Feed;
import com.nowcoder.toutiao.model.Question;
import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.FeedService;
import com.nowcoder.toutiao.service.FollowService;
import com.nowcoder.toutiao.service.QuestionService;
import com.nowcoder.toutiao.service.UserService;
import com.nowcoder.toutiao.util.JedisAdapter;
import com.nowcoder.toutiao.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/16 16:09
 */
@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;


    //建立feed的data
    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<String ,String>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        //关注的评论或者是关注问题且是关注粉丝
        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(model.getEntityId());//问题Id
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandler(EventModel model) {
        // 为了测试，把model的userId随机一下
//        Random r = new Random();
//        model.setActorId(1+r.nextInt(10));

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));

        if (feed.getData() == null) {
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        // 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        // 系统队列,未登录情况
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        //feed事件包括评论、关注
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
