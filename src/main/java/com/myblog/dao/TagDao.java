package com.myblog.dao;

import com.myblog.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagDao {
    Tag findByName(String name);

    void save(Tag tag);

    List<Tag> findByPostId(Long postId);
}
