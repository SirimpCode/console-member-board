package com.github.memberboardspring.repository.post;

import com.github.memberboardspring.repository.account.QMyMember;
import com.github.memberboardspring.repository.comment.QMyComment;
import com.github.memberboardspring.repository.like.QMyLike;
import com.github.memberboardspring.web.dto.CommentResponse;
import com.github.memberboardspring.web.dto.PostListDto;
import com.github.memberboardspring.web.dto.PostResponse;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;


@RequiredArgsConstructor
public class PostQueryImpl implements PostQuery {
    private final JPAQueryFactory queryFactory;
//    private JPQLSubQuery<Long> getLikeCountSubQuery(){
//        return JPAExpressions.select(QMyLike.myLike.count())
//                .from(QMyLike.myLike);
//    }
//    private JPQLSubQuery<Long> getCommentCountForPostSubQuery(){
//        return JPAExpressions.select(QMyComment.myComment.count())
//                .from(QMyComment.myComment)
//                .where(QPost.post.postId.eq(QMyComment.myComment.post.postId));
//    }
    @Override
    public Optional<PostResponse> findByIdJoinLikeAndComment(long postId) {
//        JPQLSubQuery<Long> likeCountSubQuery = getLikeCountSubQuery()
//                .where(QMyLike.myLike.myLikePk.post.postId.eq(postId));

        //서브쿼리없이 그룹바이로 집계함수사용
        PostResponse searchPost = queryFactory.select(
                        QPost.post.postId,
                        QPost.post.contents,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.viewCount,
                        QPost.post.myMember.name,
                        QPost.post.myMember.myMemberId,
                        QMyComment.myComment.myCommentId, QMyComment.myComment.contents,
                        QMyComment.myComment.createdAt, QMyComment.myComment.myUser.name,
                        QMyLike.myLike.countDistinct()
                )
                .from(QPost.post)
                .leftJoin(QMyLike.myLike)
                .on(QMyLike.myLike.myLikePk.post.eq(QPost.post)) // 조인 조건 수정
                .leftJoin(QMyComment.myComment)
                .on(QMyComment.myComment.post.eq(QPost.post)) // 조인 조건 수정
                .leftJoin(QMyComment.myComment.myUser, QMyMember.myMember)
                .where(QPost.post.postId.eq(postId))
                .groupBy(
                        QPost.post.postId,
                        QPost.post.contents,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.viewCount,
                        QPost.post.myMember.name,
                        QPost.post.myMember.myMemberId,
                        QMyComment.myComment.myCommentId, QMyComment.myComment.contents,
                        QMyComment.myComment.createdAt, QMyComment.myComment.myUser.name
                        )
                .orderBy(QPost.post.createdAt.desc())
                .transform(
                        GroupBy.groupBy(QPost.post.postId)
                                .list(Projections.fields(PostResponse.class,
                                                QPost.post.postId,
                                                QPost.post.contents,
                                                QPost.post.title,
                                                QPost.post.createdAt,
                                                QPost.post.viewCount,
                                                QPost.post.myMember.name.as("writer"),
                                                QPost.post.myMember.myMemberId.as("writeUserId"),
                                                QMyLike.myLike.countDistinct().as("likeCount"),
                                                GroupBy.list(Projections.fields(CommentResponse.class,
                                                                QMyComment.myComment.myCommentId,
                                                                QMyComment.myComment.contents,
                                                                QMyComment.myComment.createdAt,
                                                                QMyComment.myComment.myUser.name.as("commentWriter")
                                                        )
                                                ).as("comments")
                                        )
                                )
                ).get(0);
        searchPost.getComments().removeIf(comment -> comment.getMyCommentId() == null);
        return Optional.of(searchPost);
    }

    @Override
    public void IncreaseViewCount(long postId) {
        queryFactory.update(QPost.post)
                .set(QPost.post.viewCount, QPost.post.viewCount.add(1))
                .where(QPost.post.postId.eq(postId))
                .execute();
    }

    @Override
    public Optional<PostResponse> findByIdJoinWriter(long postId) {
        PostResponse searchPost = queryFactory.select(
                Projections.fields(PostResponse.class,
                        QPost.post.postId,
                        QPost.post.contents,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.myMember.name.as("writer")
                        )
                )
                .from(QPost.post)
                .where(QPost.post.postId.eq(postId))
                .join(QPost.post.myMember, QMyMember.myMember)
                .fetchOne();
        return Optional.ofNullable(searchPost);
    }

    @Override
    public void updatePost(long postId, String title, String contents) {
        queryFactory.update(QPost.post)
                .set(QPost.post.title, title)
                .set(QPost.post.contents, contents)
                .where(QPost.post.postId.eq(postId))
                .execute();
    }
    private void createPostListQuery(){
        queryFactory.select(
                        Projections.fields(PostListDto.class,
                                QPost.post.postId,
                                QPost.post.title,
                                QPost.post.createdAt,
                                QPost.post.viewCount,
                                QPost.post.myMember.name.as("writer"),
                                QMyLike.myLike.countDistinct().as("likeCount"),
                                QMyComment.myComment.countDistinct().as("commentCount")
                        ))
                .from(QPost.post)
                .join(QMyLike.myLike)
                .on(QMyLike.myLike.myLikePk.myMember.myMemberId.eq(QMyMember.myMember.myMemberId))
                .join(QMyComment.myComment)
                .on(QMyComment.myComment.post.postId.eq(QPost.post.postId))
                .groupBy(
                        QPost.post.postId,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.viewCount,
                        QPost.post.myMember.name
                );
    }

    @Override
    public List<PostListDto> findPostByMyLike(long userPk) {
        // 게시글에 대한 좋아요 집계를 위한 alias
        QMyLike postLike = new QMyLike("postLike");
        // 사용자가 누른 좋아요 필터링을 위한 alias
        QMyLike userLike = new QMyLike("userLike");
        return queryFactory.select(
                Projections.fields(PostListDto.class,
                        QPost.post.postId,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.viewCount,
                        QPost.post.myMember.name.as("writer"),
                        postLike.countDistinct().as("likeCount"),
                        QMyComment.myComment.countDistinct().as("commentCount")
                        ))
                .from(QPost.post)
                .leftJoin(postLike)
                .on(postLike.myLikePk.post.eq(QPost.post)) // 게시물의 좋아요 개수를 찾기위한 조인
                .join(userLike)
                .on(
                        userLike.myLikePk.post.eq(QPost.post)                                  // ← 게시글 매칭
                                .and(userLike.myLikePk.myMember.myMemberId.eq(userPk))            // ← 사용자 매칭
                )//이너조인이라 웨어절 안써도됨
                .leftJoin(QMyComment.myComment)
                .on(QMyComment.myComment.post.postId.eq(QPost.post.postId))
                .groupBy(
                        QPost.post.postId,
                        QPost.post.title,
                        QPost.post.createdAt,
                        QPost.post.viewCount,
                        QPost.post.myMember.name
                )
                .fetch();
    }
}
