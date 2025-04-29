package com.github.memberboardspring.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MyLikeRepository extends JpaRepository<MyLike, MyLikePk>, MyLikeQuery {

}
