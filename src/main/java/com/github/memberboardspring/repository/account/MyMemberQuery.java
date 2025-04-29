package com.github.memberboardspring.repository.account;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MyMemberQuery {
    long updateMemberInfo(Long myMemberId, String name, String password, String mobile);
    Optional<MyMember> findByUserIdJoinRole(String userId);
    List<MyMember> findAllNotMyId(Long myMemberId);

    long updateMemberStatus(Long myMemberId);
    @Transactional
    long updateMyLoginInfo(MyMember.Status status, int failureCount, LocalDateTime lockedDate, Long myMemberId);

    void upPoint(Long myMemberId, int i);
}
