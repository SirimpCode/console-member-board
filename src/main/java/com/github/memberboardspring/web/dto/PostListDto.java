package com.github.memberboardspring.web.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDto {
    private Long postId;
    private String title;
    private String writer;
    private long viewCount;
    private LocalDateTime createdAt;
    private long likeCount;
    private long commentCount;
}
