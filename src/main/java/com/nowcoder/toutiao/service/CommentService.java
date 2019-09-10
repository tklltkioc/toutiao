package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.CommentDAO;
import com.nowcoder.toutiao.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/12 10:31
 * 评论服务、方法集合
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity ( int entityId, int entityType ) {
        return commentDAO.selectCommentByEntity ( entityId, entityType );
    }

    public int addComment ( Comment comment ) {
        comment.setContent ( HtmlUtils.htmlEscape ( comment.getContent () ) );
        comment.setContent ( sensitiveService.filter ( comment.getContent () ) );
        return commentDAO.addComment ( comment ) > 0 ? comment.getId () : 0;
    }

    public int getCommentCount ( int entityId, int entityType ) {
        return commentDAO.getCommentCount ( entityId, entityType );
    }

    public int getUserCommentCount ( int userId ) {
        return commentDAO.getUserCommentCount ( userId );
    }

    public boolean deleteComment ( int commentId ) {
        return commentDAO.updateStatus ( commentId, 1 ) > 0;
    }

    public Comment getCommentById ( int id ) {
        return commentDAO.getCommentById ( id );
    }

}
