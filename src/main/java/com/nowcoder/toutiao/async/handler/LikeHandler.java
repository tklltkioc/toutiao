package com.nowcoder.toutiao.async.handler;

import com.nowcoder.toutiao.async.EventHandler;
import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.Message;
import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.MessageService;
import com.nowcoder.toutiao.service.UserService;
import com.nowcoder.toutiao.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 14:37
 */
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message=new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());//存在自己点赞自己
        message.setCreatedDate(new Date());
        User user=userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论,http://127.0.0.1:8080/question"+model.getExt("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
