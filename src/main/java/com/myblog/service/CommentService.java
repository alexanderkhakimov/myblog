package com.myblog.service;

import com.myblog.dao.CommentDao;
import com.myblog.model.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public List<Comment> getCommentByPostId(Long postId) {
        return commentDao.findByPostId(postId);
    }

    public Comment getCommentById(Long id) {
        return commentDao.findById(id);
    }

    public void saveComment(Comment comment) {
        commentDao.save(comment);
    }

    public void updateComment(Comment comment) {
        commentDao.update(comment);
    }

    public void deleteComment(Long id) {
        commentDao.delete(id);
    }
}
