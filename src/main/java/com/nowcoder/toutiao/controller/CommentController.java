package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.model.Comment;
import com.nowcoder.toutiao.model.EntityType;
import com.nowcoder.toutiao.model.HostHolder;
import com.nowcoder.toutiao.service.CommentService;
import com.nowcoder.toutiao.service.QuestionService;
import com.nowcoder.toutiao.service.SensitiveService;
import com.nowcoder.toutiao.service.UserService;
import com.nowcoder.toutiao.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/12 11:14
 */
@Controller
public class CommentController {
    private static final Logger logger= LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    SensitiveService sensitiveService;

    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId")int questionId,
                              @RequestParam("content")String content){
        try{
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUser()!=null){
                comment.setUserId(hostHolder.getUser().getId());
            }else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
//                return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            commentService.addComment(comment);

            //更新题目里面的评论数量
            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
            //异步更新，无须事务

        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+questionId;
    }

}


