package com.github.memberboardspring.repository.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyLikeQueryImpl implements MyLikeQuery {
    private final JPAQueryFactory queryFactory;
    @Override
    public void clickLike(MyLikePk myLikePk, boolean existsLike) {
        if (existsLike) {
            queryFactory.delete(QMyLike.myLike)
                    .where(QMyLike.myLike.myLikePk.eq(myLikePk))
                    .execute();
        } else {
//            queryFactory.insert(survey)
//                    .columns(survey.id, survey.name)
//                    .values(3, "Hello").execute();
            // Insert a new like
            queryFactory.insert(QMyLike.myLike)
                    .columns(QMyLike.myLike.myLikePk)
                    .values(myLikePk)
                    .execute();
        }
    }
}
