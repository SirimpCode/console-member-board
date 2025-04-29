package com.github.memberboardspring.repository.like;


import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.repository.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@Getter
@Embeddable
@EqualsAndHashCode(of = {"post", "myMember"})
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MyLikePk implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;//JoinColumn 명시적으로 작성안하면 필드명 _id로 작성된다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_member_id")
    private MyMember myMember;

}
