package com.nowcoder.toutiao.async;

import java.util.List;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/14 11:14
 */
public interface EventHandler {
    void doHandler(EventModel model);

    List<EventType>getSupportEventTypes();

}
