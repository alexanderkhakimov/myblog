package com.myblog.dao;

import com.myblog.model.Comment;

import java.util.List;

public interface CommentDao {
    List<Comment> findByPostId(Long postId);
    void save(Comment comment);
    void update(Comment comment);
    void delete(Long id);
}
