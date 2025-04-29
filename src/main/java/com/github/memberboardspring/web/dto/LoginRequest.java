package com.github.memberboardspring.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class LoginRequest {
    private String userId;
    private String password;
}
