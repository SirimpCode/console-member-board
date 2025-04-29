package com.github.memberboardspring.repository.post;

import com.github.memberboardspring.web.dto.PostListDto;
import com.github.memberboardspring.web.dto.PostResponse;

import java.util.List;
import java.util.Optional;

public interface PostQuery {
    Optional<PostResponse> findByIdJoinLikeAndComment(long postId);

    void IncreaseViewCount(long postId);

    Optional<PostResponse> findByIdJoinWriter(long postId);

    void updatePost(long postId, String title, String contents);

    List<PostListDto> findPostByMyLike(long userPk);
}
