package com.github.memberboardspring.repository.comment;

import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.repository.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
@Entity
@Getter
@Table(name = "my_comment")
public class MyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCommentId;
    private String contents;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "my_member_id")
    private MyMember myUser;
    private LocalDateTime createdAt;

    public static MyComment createComment(long postId, String comment, MyMember loginUser) {
        MyComment myComment = new MyComment();
        myComment.post = Post.fromByPk(postId);
        myComment.contents = comment;
        myComment.myUser = loginUser;
        myComment.createdAt = LocalDateTime.now();
        return myComment;
    }
}
