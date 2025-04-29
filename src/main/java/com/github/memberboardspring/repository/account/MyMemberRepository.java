package com.github.memberboardspring.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyMemberRepository extends JpaRepository<MyMember, Long>, MyMemberQuery {
    MyMember findByName(String name);
    Optional<MyMember> findByUserId(String userId);
    boolean existsByUserId(String userId);

    boolean existsByMobile(String mobile);
}
