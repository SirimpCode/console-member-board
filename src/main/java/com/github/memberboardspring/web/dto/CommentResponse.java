package com.github.memberboardspring.web.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long myCommentId;
    private String contents;
    private String commentWriter;
    private LocalDateTime createdAt;
}
