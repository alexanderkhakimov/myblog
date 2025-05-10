package com.myblog.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Post {
    private Long id;
    private String title;
    private String imageUrl;
    private String content;
    private int likes;
    private LocalDateTime createdAt;
    private List<Comment> comments;
    private List<Tag> tags;

    public String getTagsAsText() {
        return tags == null || tags.isEmpty() ? "" : tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(","));
    }
}
