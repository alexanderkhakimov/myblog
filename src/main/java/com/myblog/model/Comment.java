package com.myblog.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}
