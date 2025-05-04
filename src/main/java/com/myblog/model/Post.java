package com.myblog.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
}
