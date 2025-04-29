package com.github.memberboardspring.web.controller;

import com.github.memberboardspring.repository.account.MyMember;
import com.github.memberboardspring.service.PlayingService;
import com.github.memberboardspring.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PlayingController {
    private final PlayingService playingService;
    private final Scanner scanner;

    @GetMapping("/play")
    public String play() {

        while (true) {
            System.out.println("================= 시작메뉴 ================");
            System.out.println("1.회원가입\t\t2.로그인\t\t3.종료");
            System.out.println("=========================================");
            System.out.print("▷ 메뉴 선택 : ");
            String menu = scanner.nextLine().strip();

            switch (menu) {
                case "1":
                    MyMember regMember = createMemberEntity();
                    int result = playingService.memberRegisterLogic(regMember);
                    if (result == 1)
                        System.out.println("\n >>> 회원가입을 축하드립니다. <<<");
                    else
                        System.out.println("\n >>> 회원가입에 실패했습니다. <<<");
                    break;
                case "2":
                    LoginRequest loginRequest = createLoginRequest();
                    try {
                        MyMember loginUser = playingService.login(loginRequest);
                        loginMenu(loginUser);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    System.out.println(">> 종료합니다. <<");
                    return "종료";
                default:
                    System.out.println(">> 잘못된 메뉴입니다. <<");
            }
        }
    }


    private void loginMenu(MyMember loginUser) {
        switch (loginUser.getMyRole().getRoleName()) {
            case ADMIN -> adminMenu(loginUser);
            case USER -> normalUserMenu(loginUser);
        }

    }

    private void adminMenu(MyMember loginUser) {
        while (true) {
            System.out.println("================= 관리자 메뉴 [" + loginUser.getName() + "님 로그인중..] ================");
            System.out.println("1.로그아웃\t\t2.회원탈퇴하기\t\t3.나의정보보기\t\t4.회원목록보기");
            System.out.println("===================================================================");
            System.out.print("▷ 메뉴 선택 : ");
            String menu = scanner.nextLine().strip();
            switch (menu) {
                case "1" -> {
                    System.out.println(">> 로그아웃 합니다. <<");
                    return;
                }
                case "2" -> {
                    System.out.println("회원탈퇴합니다.");
                    boolean withdrawal = withdrawalUser(loginUser);
                    if (withdrawal) return;
                }
                case "3" -> printMyInfo(loginUser);
                case "4" -> {
                    System.out.println("전체 회원을 조회 합니다.");
                    playingService.printAllMember(loginUser.getMyMemberId());
                }
                default -> System.out.println(">> 잘못된 메뉴입니다. <<");
            }
        }
    }

    private boolean withdrawalUser(MyMember loginUser) {
        while (true) {
            System.out.println(">> 탈퇴 후 1일간 재가입이 불가합니다. <<");
            System.out.print("정말 탈퇴하시겠습니까? (Y/N) : ");
            String yn = scanner.nextLine().strip();
            switch (yn) {
                case "Y", "y" -> {
                    System.out.println(">> 탈퇴처리합니다. <<");
                    playingService.withdrawalUser(loginUser);
                    System.out.println(">> 탈퇴가 완료되었습니다. <<");
                    return true;
                }
                case "N", "n" -> {
                    System.out.println(">> 탈퇴를 취소합니다. <<");
                    return false;
                }
                default -> System.out.println(">> 잘못된 입력입니다. <<");
            }
        }
    }

    private boolean yOrNo(String message) {
        while (true) {
            System.out.print(message + " (Y/N) : ");
            String yn = scanner.nextLine().strip();
            switch (yn) {
                case "Y", "y" -> {
                    return true;
                }
                case "N", "n" -> {
                    return false;
                }
                default -> System.out.println(">> 잘못된 입력입니다. <<");
            }
        }
    }

    private void nextMenu() {
        System.out.print("아무키나 입력하면 메인메뉴로 돌아갑니다.");
        scanner.nextLine();
    }

    private void normalUserMenu(MyMember loginUser) {
        while (true) {
            System.out.println("===================== 로그인 메뉴 [" + loginUser.getName() + "님 로그인중..] ====================");
            System.out.println("1.로그아웃\t\t2.회원탈퇴하기\t\t3.나의정보보기\t\t4.내정보수정\n5.게시물목록\t6.게시물보기\t7.게시물작성\t8.댓글작성\t9.좋아요누르기\t10.나의게시물조회\n" +
                    "11.내가좋아요누른게시물\t12.게시물 수정\t13.게시물삭제");
            System.out.println("======================================================================");
            System.out.print("▷ 메뉴 선택 : ");
            String menu = scanner.nextLine().strip();

            switch (menu) {
                case "1" -> {
                    System.out.println(">> 로그아웃 합니다. <<");
                    return;
                }
                case "2" -> {
                    System.out.println(">> 회원탈퇴합니다. <<");
                    boolean withdrawal = withdrawalUser(loginUser);
                    if (withdrawal) return;
                }
                case "3" -> {
                    printMyInfo(loginUser);
                    nextMenu();
                }
                case "4" -> {
                    updateMyInfoInput(loginUser);
                    nextMenu();
                }
                case "5" -> {
                    System.out.println(">> 게시물 목록 보기 <<");
                    SearchSortingStandard sort = createSearchSort();
                    boolean isDescending = yOrNo("내림차순으로 보시겠습니까?");
                    sort.setDescending(isDescending);
                    playingService.printPostList(sort);
                    nextMenu();
                }
                case "6" -> {
                    System.out.println(">> 게시물 보기 <<");
                    long postId = numberAndBlankCheckInput("확인할 게시물 번호", FilterMyPostDto.notIsFilter());
                    if (postId == -1) continue;
                    playingService.findPostJoinCommentsAndPrintByPostId(postId, loginUser.getMyMemberId());
                    nextMenu();
                }
                case "7" -> {
                    System.out.println(">> 게시물 작성하기 <<");
                    PostRequestDto postRequestDto = createPostDto();
                    boolean result = yOrNo(">> 게시물을 등록 하시겠습니까?");
                    if (result) {
                        playingService.registerPost(postRequestDto, loginUser);
                        System.out.println(">> 게시물 등록이 완료되었습니다. <<");
                    } else System.out.println(">> 게시물 등록이 취소되었습니다. <<");
                    nextMenu();
                }
                case "8" -> {
                    System.out.println(">> 댓글 작성하기 <<");
                    long postId = numberAndBlankCheckInput("댓글을 작성할 게시물 번호", FilterMyPostDto.notIsFilter());
                    if (postId == -1) continue;
                    String comment = blankCheckInput("입력할 댓글");
                    playingService.registerComment(postId, comment, loginUser);
                    nextMenu();
                }
                case "9" -> {
                    System.out.println(">> 좋아요를 누릅니다. 이미 누른 게시물 이라면 좋아요가 취소됩니다. <<");
                    long postId = numberAndBlankCheckInput("좋아요를 누를 게시물 번호", FilterMyPostDto.cantMyPost(loginUser.getMyMemberId(), "내가 작성한 게시물에 좋아요를 누를 수 없습니다."));
                    if (postId == -1) continue;
                    boolean existsLike = playingService.existsLike(postId, loginUser.getMyMemberId());
                    boolean yOrNo = yOrNo(existsLike ? "좋아요를 취소 하시겠습니까?" : "좋아요를 누르시겠습니까?");
                    if (!yOrNo) {
                        System.out.println("좋아요 버튼 조작을 취소합니다.");
                        continue;
                    }

                    playingService.clickLike(existsLike, postId, loginUser.getMyMemberId());
                    nextMenu();
                }
                case "10" -> {
                    System.out.println(">> 나의 게시물 목록 보기 <<");
                    playingService.findMyPostAndPrintByLoginId(loginUser.getMyMemberId());
                    nextMenu();
                }
                case "11" ->{
                    playingService.findMyPostAndPrintByMyLike(loginUser.getMyMemberId());
                    nextMenu();
                }


                case "12" -> {
                    System.out.println(">> 게시물 수정하기 <<");
                    long postId = numberAndBlankCheckInput("수정할 게시물 번호",
                            FilterMyPostDto.onlyAccessMyPost(loginUser.getMyMemberId(),
                                    "내가 작성한 게시물만 수정이 가능합니다."));
                    if (postId == -1) continue;

                    System.out.println(">> 수정할 게시물의 내용 입니다. <<");
                    PostResponse originPost = playingService.findPostAndPrintByPostId(postId);
                    updateMyPostInputAndApply(originPost);
                    System.out.println(">> 게시물 수정이 완료 되었습니다. <<");
                    nextMenu();
                }
                case "13" ->{
                    System.out.println(">> 게시물 삭제하기 <<");
                    long postId = numberAndBlankCheckInput("삭제할 게시물 번호",
                            FilterMyPostDto.onlyAccessMyPost(loginUser.getMyMemberId(),
                                    "내가 작성한 게시물만 삭제가 가능합니다."));
                    if (postId == -1) continue;
                    System.out.println(">> 삭제할 게시물의 내용 입니다. <<");
                    playingService.findPostJoinCommentsAndPrintByPostId(postId, loginUser.getMyMemberId());
                    boolean result = yOrNo("게시물을 삭제 하시겠습니까? 삭제시 댓글들도 전부 삭제됩니다.");
                    if (result) {
                        playingService.deletePostBy(postId);
                        System.out.println(">> 게시물 삭제가 완료되었습니다. <<");
                    } else System.out.println(">> 게시물 삭제가 취소되었습니다. <<");
                    nextMenu();
                }

                default -> System.out.println(">> 잘못된 메뉴입니다. <<");
            }
        }
    }

    private SearchSortingStandard createSearchSort() {
        System.out.println(">> 보여질 목록의 정렬기준을 선택해 주세요. <<");
        System.out.println("1.최신순\t\t2.조회수순\t\t3.좋아요순\t\t4.제목순\t\t5.댓글순");
        while(true){
            String menu = scanner.nextLine().strip();
            switch (menu) {
                case "1" -> {
                    return SearchSortingStandard.LATEST;
                }
                case "2" -> {
                    return SearchSortingStandard.VIEWS;
                }
                case "3" -> {
                    return SearchSortingStandard.LIKES;
                }
                case "4" -> {
                    return SearchSortingStandard.TITLE;
                }
                case "5" -> {
                    return SearchSortingStandard.COMMENTS;
                }
                default -> System.out.println(">> 잘못된 메뉴입니다. <<");
            }

        }

    }

    private PostRequestDto createPostDto() {
        String title = blankCheckInput("제목");
        String contents = blankCheckInput("내용");
        return PostRequestDto.of(title, contents);
    }
    private void updateMyPostInputAndApply(PostResponse postResponse) {
        String title = postResponse.getTitle();
        String contents = postResponse.getContents();
        while(true){
            System.out.println(">> [게시물 수정 메뉴] 수정할 것을 선택해 주세요. <<");
            System.out.println("1.제목\t\t2.내용\t\t3.수정완료\t\t4.수정취소");
            System.out.print("▷ 메뉴 선택 : ");
            String menu = scanner.nextLine().strip();
            switch (menu) {
                case "1" -> {
                    title = blankCheckInput("수정할 제목");
                    System.out.println(">> 제목이 변경되었습니다. <<");
                }
                case "2" -> {
                    contents = blankCheckInput("수정할 내용");
                    System.out.println(">> 게시물 내용이 변경되었습니다. <<");
                }
                case "3" -> {
                    System.out.println(">> 수정된 정보를 적용합니다. <<");
                    postResponse.modifyPost(title, contents);
                    playingService.updatePost(postResponse);
                    System.out.println(">> 게시물 수정이 완료되었습니다. <<");
                    return;
                }
                case "4" -> {
                    System.out.println(">> 게시물 수정을 취소합니다. <<");
                    return;
                }
                default -> System.out.println(">> 잘못된 메뉴입니다. <<");
            }
        }



    }

    private void updateMyInfoInput(MyMember loginUser) {
        String password = loginUser.getPassword();
        String name = loginUser.getName();
        String phone = loginUser.getMobile();
        while (true) {
            System.out.println(">> 내 정보 수정 메뉴 <<");
            System.out.println("1.비밀번호변경\t\t2.이름변경\t\t3.전화번호변경\t\t4.수정완료\t\t5.수정취소");
            System.out.print("▷ 메뉴 선택 : ");
            String menu = scanner.nextLine().strip();
            switch (menu) {
                case "1" -> {
                    password = inputPassword();
                    System.out.println(">> 비밀번호가 변경되었습니다. <<");
                }
                case "2" -> {
                    name = blankCheckInput("이름");
                    System.out.println(">> 이름이 변경되었습니다. <<");
                }
                case "3" -> {
                    phone = phoneCheckInput();
                    System.out.println(">> 전화번호가 변경되었습니다. <<");
                }
                case "4" -> {
                    System.out.println(">> 수정된 정보를 적용합니다. <<");
                    loginUser.modifyInfo(password, name, phone);
                    playingService.updateMyInfo(loginUser);
                    System.out.println(">> 내정보수정이 완료되었습니다. <<");
                    return;
                }
                case "5" -> {
                    System.out.println(">> 내정보수정을 취소합니다. <<");
                    return;
                }
                default -> System.out.println(">> 잘못된 메뉴입니다. <<");
            }
        }

    }

    private void printMyInfo(MyMember loginUser) {
        System.out.println("=============>> 나의정보보기 <<=============");
        System.out.println("▷ 아이디 : " + loginUser.getUserId());
        System.out.println("▷ 이름 : " + loginUser.getName());
        System.out.println("▷ 전화번호 : " + loginUser.getMobile().substring(0, 3) + "-" + loginUser.getMobile().substring(3, 7) + "-" + loginUser.getMobile().substring(7));
        System.out.println("▷ 포인트 : " + loginUser.getPoint());
        System.out.println("▷ 가입일 : " + loginUser.getRegisterDay());
        if (loginUser.getLockedDate() != null) {
            System.out.println("▷ 잠김일 : " + loginUser.getLockedDate());
        }
        if (loginUser.getWithdrawalDate() != null) {
            System.out.println("▷ 탈퇴일 : " + loginUser.getWithdrawalDate());
        }

        System.out.println("=========================================");

    }

    private LoginRequest createLoginRequest() {
        String id = blankCheckInput("아이디");
        String password = blankCheckInput("비밀번호");
        return LoginRequest.of(id, password);
    }

    private MyMember createMemberEntity() {
        String id = idCheckInput();
        String password = inputPassword();
        String name = blankCheckInput("이름");
        String phone = phoneCheckInput();
        return MyMember.regMember(id, password, name, phone);

    }

    private String blankCheckInput(String msg) {
        while (true) {
            System.out.print(msg + " : ");
            String input = scanner.nextLine().strip();
            if (input.isBlank()) {
                System.out.println("입력값이 없습니다.");
                continue;
            }
            return input;
        }
    }

    private String phoneCheckInput() {
        while (true) {
            System.out.print("전화번호 : ");
            String phone = scanner.nextLine().strip();
            if (phone.isBlank()) {
                System.out.println("입력값이 없습니다.");
                continue;
            }
            if (!phone.matches("^01[0-9]{9}$")) {
                System.out.println("01 로 시작하는 전화번호 11자리를 입력해주세요.");
                continue;
            }
            if (playingService.existsMobile(phone)) {
                System.out.println("이미 존재하는 전화번호입니다.");
                continue;
            }
            return phone;
        }
    }

    private long numberAndBlankCheckInput(String message, FilterMyPostDto filterMyPostDto) {

        while (true) {
            System.out.print(message + "(뒤로가기는 exit 입력) : ");
            String input = scanner.nextLine().strip();
            if (input.equalsIgnoreCase("exit"))
                return -1;
            if (input.isBlank()) {
                System.out.println("입력값이 없습니다.");
                continue;
            }
            try {
                long number = Long.parseLong(input);
                boolean validPostId = playingService.existsPostId(number);
                if (!validPostId) {
                    System.out.println("존재하지 않는 게시물입니다.");
                    continue;
                }
                if (!filterMyPostDto.isFilter())
                    return number;//필터링 할필요가없으면 바로 반환
                boolean cantMyPost = filterMyPostDto.isCantMyPost();
                boolean isMyPost = playingService.isMyPost(number, filterMyPostDto.getLoginUserId());
                //dto 의 필드 cantMyPost 로 분기시킨다.
                if (cantMyPost == isMyPost) {//내가 작성한 게시물이라면 또는 내가 작성한 게시물이 아니라면
                    System.out.println(filterMyPostDto.getErrorMessage());
                    continue;
                }
                return number;
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력 가능합니다.");
            }
        }
    }

    private String idCheckInput() {
        while (true) {
            System.out.print("아이디 : ");
            String id = scanner.nextLine().strip();
            if (id.isBlank()) {
                System.out.println("입력값이 없습니다.");
                continue;
            }
            if (!id.matches("^[a-z][a-z0-9]{3,9}$")) {
                System.out.println("영문 소문자로 시작하고 소문자와 숫자로만 조합된 4~10자리의 아이디만 가능합니다.");
                continue;
            }
            if (playingService.existsId(id)) {
                System.out.println("이미 존재하는 아이디입니다.");
                continue;
            }
            return id;
        }
    }

    private String inputPassword() {
        while (true) {
            System.out.print("비밀번호 : ");
            String password = scanner.nextLine().strip();
            if (password.isBlank()) {
                System.out.println("입력값이 없습니다.");
                continue;
            }
            if (!password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])[0-9a-zA-Z!@#$%^&*(),.?\":{}|<>]{8,20}$")) {
                System.out.println("영문 대소문자, 숫자 조합 8~16자리로 입력해주세요.");
                continue;
            }
            return password;
        }
    }


    @GetMapping("/test")
    public Object test() {
        return playingService.repositoryTest();
    }
}
