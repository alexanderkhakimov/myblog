package com.myblog.service;

import com.myblog.dao.TagDao;
import com.myblog.model.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Tag> processTags(String tagsString) {
        String[] tagArray = tagsString.split("\\s*,\\s*");
        List<Tag> tagList = new ArrayList<>();

        for (String name : tagArray) {
            if (!name.isEmpty()) {
                if (name.length() > 50) {
                    return null; // сигнализируем об ошибке валидации
                }

                // Проверяем, существует ли тег в базе
                Tag existingTag = findByName(name);
                if (existingTag != null) {
                    tagList.add(existingTag);
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    saveTag(newTag);
                    tagList.add(newTag);
                }
            }
        }
        return tagList;
    }
}
