package com.myblog.dao;

import com.myblog.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDao {
    List<Comment> findByPostId(Long postId);
    void save(Comment comment);
    void update(Comment comment);
    void delete(Long id);
}
