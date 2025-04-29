package com.github.memberboardspring.repository.like;


import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.repository.post.Post;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "myLikePk")
@DynamicInsert
@Getter
@Table(name = "my_like")
public class MyLike {
    @EmbeddedId
    private MyLikePk myLikePk;

    private LocalDateTime createdAt;
    public static MyLike fromByPk(Long postId, Long myMemberId) {
        MyLike myLike = new MyLike();
        myLike.myLikePk = MyLikePk.of(
                Post.fromByPk(postId), MyMember.fromByPk(myMemberId));
        return myLike;
    }
}
