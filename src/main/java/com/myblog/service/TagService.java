package com.myblog.service;

import com.myblog.dao.TagDao;
import com.myblog.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagDao tagDao;

    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public Tag findByName(String name) {
        return tagDao.findByName(name);
    }

    public void saveTag(Tag tag) {
        tagDao.save(tag);
    }

    public List<Tag> findByPostId(Long postId) {
        return tagDao.findByPostId(postId);
    }
}
