package com.myblog.dao;

import com.myblog.model.Tag;

import java.util.List;

public interface TagDao {
    Tag findByName(String name);

    void save(Tag tag);

    List<Tag> findByPostId(Long postId);
}
