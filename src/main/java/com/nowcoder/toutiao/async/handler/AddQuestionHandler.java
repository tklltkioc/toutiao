package com.nowcoder.toutiao.async.handler;

import com.nowcoder.toutiao.async.EventHandler;
import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/18 21:04
 * 发布问题事件
 */
@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;

    @Override
    public void doHandler (EventModel model) {
        try {
            searchService.indexQuestion(model.getEntityId(),
                    model.getExt("title"), model.getExt("content"));
        } catch (Exception e) {
            logger.error("增加题目索引失败" + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes () {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
