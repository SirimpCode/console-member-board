package com.github.memberboardspring.repository.post;

import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.repository.comment.MyComment;
import jakarta.persistence.*;
import lombok.Getter;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @ManyToOne
    @JoinColumn(name = "my_member_id")
    private MyMember myMember;
    private String title;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MyComment> myComments;
    private String contents;
    private LocalDateTime createdAt;
    private long viewCount;

    public static Post fromByPk(Long postId) {
        Post post = new Post();
        post.postId = postId;
        return post;
    }


    public static Post createPost(String title, String contents, MyMember loginUser) {
        Post post = new Post();
        post.title = title;
        post.contents = contents;
        post.myMember = loginUser;
        post.createdAt = LocalDateTime.now();
        post.viewCount = 0;
        return post;
    }
}
