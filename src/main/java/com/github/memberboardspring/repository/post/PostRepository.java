package com.github.memberboardspring.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostQuery {
    boolean existsByPostIdAndMyMember_MyMemberId(long postId, long myMemberId);

    List<Post> findByMyMember_MyMemberId(long loginId);


}
