package com.github.memberboardspring.repository.account;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MyMemberQueryImpl implements MyMemberQuery {
    private final JPAQueryFactory queryFactory;
    private final QMyMember qMyMember = QMyMember.myMember;
    private final EntityManager em;
    @Override
    public long updateMemberInfo(Long myMemberId, String name, String password, String mobile) {
        return queryFactory.update(qMyMember)
                .set(qMyMember.name, name)
                .set(qMyMember.password, password)
                .set(qMyMember.mobile, mobile)
                .where(qMyMember.myMemberId.eq(myMemberId))
                .execute();
    }

    @Override
    public Optional<MyMember> findByUserIdJoinRole(String userId) {
        MyMember myMember = queryFactory.selectFrom(qMyMember)
                .join(qMyMember.myRole,QMyRole.myRole).fetchJoin()
                .where(qMyMember.userId.eq(userId)).fetchOne();
        return Optional.ofNullable(myMember);
    }

    @Override
    public List<MyMember> findAllNotMyId(Long myMemberId) {
        return queryFactory.selectFrom(qMyMember)
                .where(qMyMember.myMemberId.ne(myMemberId))
                .fetch();
    }

    @Override
    public long updateMemberStatus(Long myMemberId) {
        return queryFactory.update(qMyMember)
                .set(qMyMember.status, MyMember.Status.WITHDRAWAL)
                .where(qMyMember.myMemberId.eq(myMemberId))
                .execute();
    }

    @Override
    public long updateMyLoginInfo(MyMember.Status status, int failureCount, LocalDateTime lockedDate, Long myMemberId) {
        return queryFactory.update(QMyMember.myMember)
                .set(qMyMember.status, status)
                .set(qMyMember.failureCount, failureCount)
                .set(qMyMember.lockedDate, lockedDate)
                .where(qMyMember.myMemberId.eq(myMemberId))
                .execute();
    }

    @Override
    public void upPoint(Long myMemberId, int upPoint) {
        int downPoint = -upPoint;
        queryFactory.update(qMyMember)//subtract, multiply, divide  빼기 곱하기 나누기
                .set(qMyMember.point, qMyMember.point.add(upPoint))
                .where(qMyMember.myMemberId.eq(myMemberId))
                .execute();

    }
}
