package com.github.memberboardspring.repository.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Entity
public class MyRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myRoleId;
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @Getter
    @AllArgsConstructor
    public enum RoleName {
        ADMIN("운영자"), USER("유저");
        private final String value;

    }
}
