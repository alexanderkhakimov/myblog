package com.myblog.dao;

import com.myblog.model.Post;

import java.util.List;

public interface PostDao {
    List<Post> findAll (int page, int size, String tag);
    Post findById(Long id);
    void save(Post post);
    void update(Post post);
    void delete(Long id);
    void incrementLikes(Long id);

}
