package com.github.memberboardspring.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class PostRequestDto {
    private String title;
    private String contents;
}
