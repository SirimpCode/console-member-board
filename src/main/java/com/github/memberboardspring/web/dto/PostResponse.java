package com.github.memberboardspring.web.dto;

import com.github.memberboardspring.repository.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponse {
    private long postId;
    private long writeUserId;
    private String title;
    private String contents;
    private String writer;
    private LocalDateTime createdAt;
    @Setter
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private boolean isLike;
    private List<CommentResponse> comments;

    public void settingCommentCount(){
        this.commentCount = comments.size();
    }
    public void modifyPost(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
    public static PostResponse PostConvertPostResponse(Post post){
        PostResponse postResponse = new PostResponse();
        postResponse.postId = post.getPostId();
        postResponse.writeUserId = post.getMyMember().getMyMemberId();
        postResponse.title = post.getTitle();
        postResponse.contents = post.getContents();
        postResponse.writer = post.getMyMember().getName();
        postResponse.createdAt = post.getCreatedAt();
        return postResponse;
    }

}
