package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventProducer;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.*;
import com.nowcoder.toutiao.service.CommentService;
import com.nowcoder.toutiao.service.FollowService;
import com.nowcoder.toutiao.service.QuestionService;
import com.nowcoder.toutiao.service.UserService;
import com.nowcoder.toutiao.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/15 16:45
 */
@Controller
public class FollowController {
//    private static final Logger logger= LoggerFactory.getLogger(FollowController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    //当前用户进行关注的用户
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser (@RequestParam("userId") int userId) {
        if (hostHolder.getUser () == null) {
            return WendaUtil.getJSONString (999);
        }

        boolean ret = followService.follow (hostHolder.getUser ().getId (), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent (new EventModel (EventType.FOLLOW)
                .setActorId (hostHolder.getUser ().getId ()).setEntityId (userId)
                .setEntityType (EntityType.ENTITY_USER).setEntityOwnerId (userId));

        // 返回关注的人数
        return WendaUtil.getJSONString (ret ? 0 : 1, String.valueOf (followService.getFolloweeCount (hostHolder.getUser ().getId (), EntityType.ENTITY_USER)));
    }

    //当前用户取消用户关注
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowUser (@RequestParam("userId") int userId) {
        if (hostHolder.getUser () == null) {
            return WendaUtil.getJSONString (999);
        }

        boolean ret = followService.unfollow (hostHolder.getUser ().getId (), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent (new EventModel (EventType.UNFOLLOW)
                .setActorId (hostHolder.getUser ().getId ()).setEntityId (userId)
                .setEntityType (EntityType.ENTITY_USER).setEntityOwnerId (userId));

        // 返回关注的人数
        return WendaUtil.getJSONString (ret ? 0 : 1, String.valueOf (followService.getFolloweeCount (hostHolder.getUser ().getId (), EntityType.ENTITY_USER)));
    }

    //当前用户关注的问题
    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followQuestion (@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser () == null) {
            return WendaUtil.getJSONString (999);
        }

        Question q = questionService.getById (questionId);
        if (q == null) {
            return WendaUtil.getJSONString (1, "问题不存在");
        }

        boolean ret = followService.follow (hostHolder.getUser ().getId (), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent (new EventModel (EventType.FOLLOW)
                .setActorId (hostHolder.getUser ().getId ()).setEntityId (questionId)
                .setEntityType (EntityType.ENTITY_QUESTION).setEntityOwnerId (q.getUserId ()));

        Map<String, Object> info = new HashMap<> ();
        //follower返回信息给前端
        info.put ("headUrl", hostHolder.getUser ().getHeadUrl ());
        info.put ("name", hostHolder.getUser ().getName ());
        info.put ("id", hostHolder.getUser ().getId ());
        info.put ("count", followService.getFollowerCount (EntityType.ENTITY_QUESTION, questionId));

        // 返回关注的问题信息
        return WendaUtil.getJSONString (ret ? 0 : 1, info);
    }

    //当前用户取消问题关注
    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowQuestion (@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser () == null) {
            return WendaUtil.getJSONString (999);
        }

        Question q = questionService.getById (questionId);
        if (q == null) {
            return WendaUtil.getJSONString (1, "问题不存在");
        }

        boolean ret = followService.unfollow (hostHolder.getUser ().getId (), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent (new EventModel (EventType.UNFOLLOW)
                .setActorId (hostHolder.getUser ().getId ()).setEntityId (questionId)
                .setEntityType (EntityType.ENTITY_QUESTION).setEntityOwnerId (q.getUserId ()));

        Map<String, Object> info = new HashMap<> ();
        info.put ("id", hostHolder.getUser ().getId ());
        info.put ("count", followService.getFollowerCount (EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString (ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers (Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers (EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser () != null) {
            model.addAttribute ("followers", getUsersInfo (hostHolder.getUser ().getId (), followerIds));
        } else {
            model.addAttribute ("followers", getUsersInfo (0, followerIds));
        }
        model.addAttribute ("followerCount", followService.getFollowerCount (EntityType.ENTITY_USER, userId));
        model.addAttribute ("curUser", userService.getUser (userId));
        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees (Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees (userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHolder.getUser () != null) {
            model.addAttribute ("followees", getUsersInfo (hostHolder.getUser ().getId (), followeeIds));
        } else {
            model.addAttribute ("followees", getUsersInfo (0, followeeIds));
        }
        model.addAttribute ("followeeCount", followService.getFolloweeCount (userId, EntityType.ENTITY_USER));
        model.addAttribute ("curUser", userService.getUser (userId));
        return "followees";
    }

    //本地用户关注的用户
    private List<ViewObject> getUsersInfo (int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject> ();
        for (Integer uid : userIds) {
            User user = userService.getUser (uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject ();
            vo.set ("user", user);
            vo.set ("commentCount", commentService.getUserCommentCount (uid));
            vo.set ("followerCount", followService.getFollowerCount (EntityType.ENTITY_USER, uid));
            vo.set ("followeeCount", followService.getFolloweeCount (uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set ("followed", followService.isFollower (localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set ("followed", false);
            }
            userInfos.add (vo);
        }
        return userInfos;
    }
}
