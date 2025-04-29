package com.github.memberboardspring.service;

import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.repository.account.MyMemberRepository;
import com.github.memberboardspring.repository.account.MyRole;
import com.github.memberboardspring.repository.account.MyRoleRepository;
import com.github.memberboardspring.repository.comment.MyComment;
import com.github.memberboardspring.repository.comment.MyCommentRepository;
import com.github.memberboardspring.repository.like.MyLikePk;
import com.github.memberboardspring.repository.like.MyLikeRepository;
import com.github.memberboardspring.repository.post.Post;
import com.github.memberboardspring.repository.post.PostRepository;
import com.github.memberboardspring.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayingService {
    private final MyMemberRepository myMemberRepository;
    private final MyRoleRepository myRoleRepository;
    private final MyLikeRepository myLikeRepository;
    private final PostRepository postRepository;
    private final MyCommentRepository myCommentRepository;
    public List<MyRole> repositoryTest(){
        List<MyRole> member = myRoleRepository.findAll();
        return member;
    }
    @Transactional
    public void updateMyInfo(MyMember loginUser) {
        long result = myMemberRepository.updateMemberInfo(loginUser.getMyMemberId(), loginUser.getName(), loginUser.getPassword(), loginUser.getMobile());

        if (result == 1) {
            return;
        }
        System.out.println(">> 회원정보 수정 실패 <<");
        throw new IllegalStateException(">> 회원정보 수정 실패 <<");

    }

    public void printAllMember(Long myMemberId) {
        List<MyMember> allMember = myMemberRepository.findAllNotMyId(myMemberId);
        allMember.forEach(member -> {
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.printf("회원번호 : %d\t아이디 : %s\t비밀번호 : %s\t이름 : %s\t전화번호 : %s\t가입일자 : %s\t상태 : %s\t역할 : %s\n",
                    member.getMyMemberId(), member.getUserId(), member.getPassword(), member.getName(), member.getMobile(),
                    member.getRegisterDay(), member.getStatus().getValue(), member.getMyRole().getRoleName().getValue());
        });
    }

    public int memberRegisterLogic(MyMember regMember) {
        myMemberRepository.save(regMember);
        return 1;
    }
    public MyMember login(LoginRequest loginRequest) {
        MyMember loginUser = myMemberRepository.findByUserIdJoinRole(loginRequest.getUserId())
                .orElseThrow(()-> new IllegalArgumentException(">> 해당 아이디가 존재하지 않습니다. <<"));
        return switch(loginUser.getStatus()) {
            case NORMAL -> {
                if(!loginUser.getPassword().equals(loginRequest.getPassword()))
                    passwordFailSuccLogic(loginUser, false);
                passwordFailSuccLogic(loginUser, true);
                yield loginUser;
            }
            case LOCKED -> {
                LocalDateTime lockedDate = loginUser.getLockedDate();
                LocalDateTime now = LocalDateTime.now();
                if(lockedDate.plusMinutes(3).isAfter(now)) {
                    String remainingTime = getRemainingTime(loginUser.getLockedDate());
                    throw new IllegalArgumentException("\n>> 계정이 잠겼습니다. 남은 시간 ["+remainingTime+"] <<");
                }
                if(!loginUser.getPassword().equals(loginRequest.getPassword()))
                    passwordFailSuccLogic(loginUser, false);


                passwordFailSuccLogic(loginUser, true);
                yield loginUser;
            }
            case WITHDRAWAL -> {
                if(!loginUser.getPassword().equals(loginRequest.getPassword()))
                    throw new IllegalArgumentException(">> 비밀번호가 틀립니다. <<");

                LocalDateTime withdrawalDate = loginUser.getWithdrawalDate();
                if(withdrawalDate.plusDays(10).isBefore(LocalDateTime.now()))
                    yield loginUser;

                throw new IllegalArgumentException(">> 탈퇴한 계정입니다. 해당 아이디로는 1일 후 재가입이 가능합니다.<<");
            }
        };
    }
    private String getRemainingTime(LocalDateTime lockedDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, lockedDate.plusMinutes(3));
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;

        return String.format("%02d분%02d초", minutes, seconds);
    }

    private void passwordFailSuccLogic(MyMember member, boolean isSuccess) {
        member.loginValueSetting(isSuccess);
        long result = myMemberRepository.updateMyLoginInfo(member.getStatus(), member.getFailureCount(), member.getLockedDate(), member.getMyMemberId());
        if (result != 1)
            throw new IllegalStateException(">> 로그인 정보 업데이트 실패 <<");
        if(!isSuccess) {
            if(member.getStatus() == MyMember.Status.LOCKED) {
                String remainingTime = getRemainingTime(member.getLockedDate());
                throw new IllegalArgumentException("\n>> 계정이 잠겼습니다. 남은 시간 ["+remainingTime+"] <<");
            }
            throw new IllegalArgumentException("\n>> 비밀번호가 틀립니다. "+(5-member.getFailureCount())+"번 더 틀릴시 3분간 계정이 잠깁니다.<<");
        }

    }

    @Transactional
    public void withdrawalUser(MyMember loginUser) {
        long result = myMemberRepository.updateMemberStatus(loginUser.getMyMemberId());
        if (result == 1) return;
        throw new IllegalStateException(">> 회원정보 수정 실패 <<");
    }

    public boolean existsId(String id) {
        return myMemberRepository.existsByUserId(id);
    }
    public boolean existsMobile(String mobile) {
        return myMemberRepository.existsByMobile(mobile);
    }
    @Transactional
    public void registerPost(PostRequestDto postRequestDto, MyMember loginUser) {
        Post post = Post.createPost(postRequestDto.getTitle(), postRequestDto.getContents(), loginUser);
        myMemberRepository.upPoint(loginUser.getMyMemberId(), 10);
        Post createdPost = postRepository.save(post);
        System.out.println(">> 등록된 게시물 정보 입니다. <<");
        simplePostPrinter(PostResponse.PostConvertPostResponse(createdPost));
    }

    public boolean existsPostId(long number) {
        return postRepository.existsById(number);
    }
    @Transactional(readOnly = true)
    public void printPostList(){
        List<Post> postList = postRepository.findAll();
        postListPrinter(postList);

    }

    private void postDtoListPrinter(List<PostListDto> myLikePostList) {
        myLikePostList.forEach(post->{
            String title = post.getTitle().length()<=10?
                    post.getTitle() :
                    post.getTitle().substring(0, 10) + "...";
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.printf("게시글 번호 : %d\t\t제목 : %s\t\t작성자 : %s\t\t작성일자 : %s\n",
                    post.getPostId(),
                    title,
                    post.getWriter(),
                    post.getCreatedAt());
        });
        System.out.println("----------------------------------------------------------------------------------------------");
    }
    private void postListPrinter(List<Post> postList) {
        postList.forEach(post -> {

            String title = post.getTitle().length()<=10?
                    post.getTitle() :
                    post.getTitle().substring(0, 10) + "...";
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.printf("게시글 번호 : %d\t\t제목 : %s\t\t작성자 : %s\t\t작성일자 : %s\n",
                    post.getPostId(),
                    title,
                    post.getMyMember().getName(),
                    post.getCreatedAt());
        });
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    private void detailPrinter(PostResponse postResponse) {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println("게시글 번호 : "+postResponse.getPostId()+"\n" +
                "조회수 : "+postResponse.getViewCount()+"\n" +
                "좋아요 수 : "+postResponse.getLikeCount()+"\n" +
                "댓글 수 : "+postResponse.getCommentCount()+"\n" +
                "제목 : "+postResponse.getTitle()+"\n" +
                "작성자 : "+postResponse.getWriter()+"\n" +
                "작성일자 : "+postResponse.getCreatedAt()+"\n" +
                "내용 : "+postResponse.getContents()
        );
    }
    private void simplePostPrinter(PostResponse postResponse) {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("게시글 번호 : %d\n제목 : %s\n내용 : %s\n작성자 : %s\n작성일자 : %s\n",
                postResponse.getPostId(),
                postResponse.getTitle(),
                postResponse.getContents(),
                postResponse.getWriter(),
                postResponse.getCreatedAt());
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    private void commentListPrinter(List<CommentResponse> commentList){
        System.out.println("---------------------------------------------------->> 댓글 <<--------------------------------");
        commentList.forEach(comment -> {
            System.out.println("\t댓글 번호 : "+comment.getMyCommentId()+"\n" +
                    "\t작성자 : "+comment.getCommentWriter()+"\n" +
                    "\t작성일자 : "+comment.getCreatedAt()+"\n" +
                    "\t내용 : "+comment.getContents());
            System.out.println("----------------------------------------------------------------------------------------------");
        });
    }
    public PostResponse findPostAndPrintByPostId(long postId){
        PostResponse post = postRepository.findByIdJoinWriter(postId)
                .orElseThrow(() -> new IllegalArgumentException(">> 해당 게시글이 존재하지 않습니다. <<"));
        simplePostPrinter(post);
        return post;
    }

    @Transactional(readOnly = true)
    public void findPostJoinCommentsAndPrintByPostId(long postId, long loginUserId) {
        PostResponse post = postRepository.findByIdJoinLikeAndComment(postId)
                .orElseThrow(() -> new IllegalArgumentException(">> 해당 게시글이 존재하지 않습니다. <<"));
        post.settingCommentCount();
        if(loginUserId != post.getWriteUserId())
            updateViewCount(post);
        detailPrinter(post);//게시물이후 댓글 출력
        commentListPrinter(post.getComments());
    }
    private void updateViewCount(PostResponse post) {
        postRepository.IncreaseViewCount(post.getPostId());
        post.setViewCount(post.getViewCount() + 1);
    }

    @Transactional(readOnly = true)
    public void findMyPostAndPrintByLoginId(long loginId){
        List<Post> myPostList = postRepository.findByMyMember_MyMemberId(loginId);
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("내가 작성한 게시글 목록 (총 %d개)\n", myPostList.size());
        postListPrinter(myPostList);
    }
    private void commentPrinter(MyComment comment) {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("댓글 번호 : %d\t\t내용 : %s\t\t작성자 : %s\t\t작성일자 : %s\t\t작성한 게시물 번호 : %d\n",
                comment.getMyCommentId(),
                comment.getContents(),
                comment.getMyUser().getName(),
                comment.getCreatedAt(),
                comment.getPost().getPostId());

        System.out.println("----------------------------------------------------------------------------------------------\n");
    }
    @Transactional
    public void registerComment(long postId, String comment, MyMember loginUser) {
        MyComment newComment = MyComment.createComment(postId, comment, loginUser);
        MyComment createdComment = myCommentRepository.save(newComment);
        System.out.println("\n--------------------------------->> 입력한 댓글 <<-----------------------------------------------");
        commentPrinter(createdComment);
    }

    public boolean existsLike(long postId, Long myMemberId) {
        Post post = Post.fromByPk(postId);
        MyMember myMember = MyMember.fromByPk(myMemberId);
        MyLikePk myLikePk = MyLikePk.of(post, myMember);
        return myLikeRepository.existsById(myLikePk);
    }
    @Transactional
    public void clickLike(boolean existsLike, long postId, long loginUserId) {
        MyLikePk myLikePk = MyLikePk.of(Post.fromByPk(postId), MyMember.fromByPk(loginUserId));
        myLikeRepository.clickLike(myLikePk, existsLike);

        if(existsLike) {
            System.out.println(">> 좋아요 취소 <<");
            return;
        }
        System.out.println(">> 좋아요 등록 <<");
    }
    @Transactional(readOnly = true)
    public boolean isMyPost(long postId, long loginUserPk) {
        return postRepository.existsByPostIdAndMyMember_MyMemberId(postId, loginUserPk);
    }

    @Transactional
    public void updatePost(PostResponse postResponse) {
        postRepository.updatePost(postResponse.getPostId(), postResponse.getTitle(), postResponse.getContents());
    }

    public void deletePostBy(long postId) {
        postRepository.deleteById(postId);
    }
    @Transactional(readOnly = true)
    public void findMyPostAndPrintByMyLike(long userPk) {
        List<PostListDto> myLikePostList = postRepository.findPostByMyLike(userPk);
        if(myLikePostList.isEmpty()){
            System.out.println(">> 내가 좋아요 누른 게시물이 없습니다. <<");
            return;
        }
        System.out.println(">> 내가 좋아요 누른 게시물 목록 <<");
        postDtoListPrinter(myLikePostList);
    }

}
