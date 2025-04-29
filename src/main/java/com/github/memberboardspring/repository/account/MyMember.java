package com.github.memberboardspring.repository.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "my_member")
@DynamicInsert
@NoArgsConstructor
public class MyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myMemberId;
    @Column(unique=true,nullable = false, length = 30)
    private String userId;
    @Column(nullable = false, length = 30)
    private String password;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(length = 11)
    private String mobile;
    private int point;
    private LocalDateTime registerDay;
    private LocalDateTime lockedDate;
    private LocalDateTime withdrawalDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_role_id")
    private MyRole myRole;
    @Enumerated(EnumType.STRING)
    private Status status;

    private int failureCount;

    public MyMember(String id, String password, String name, String phone) {
        this.userId = id;
        this.password = password;
        this.name = name;
        this.mobile = phone;
    }

    public static MyMember regMember(String id, String password, String name, String phone) {
        return new MyMember(id,password,name,phone);
    }

    public void modifyInfo(String password, String name, String phone) {
        this.password = password;
        this.name = name;
        this.mobile = phone;
    }
    public void loginValueSetting(boolean isSuccess) {
        //5번째 시도이고 5분이내 한번 더 시도했을시 잠금처리
        this.status = isSuccess ?
                Status.NORMAL
                : (isFailureCountingOrLocking() || isUnlockTime() ? Status.NORMAL : Status.LOCKED);

        //실패시 failureCount 를 1 증가시킨다. 단 계정이 잠길땐 0으로 만들고, 실패한지 5분 이상 지났을시 1부터 다시시작
        this.failureCount = isSuccess ?
                0
                :(isUnlockTime() ?
                1 : (isFailureCountingOrLocking() ? failureCount + 1 : 0));

        this.lockedDate = isSuccess ? null : LocalDateTime.now();
    }
    private boolean isFailureCountingOrLocking() {
        return this.failureCount < 4;
    }
    private boolean isUnlockTime() {
        return this.lockedDate != null
                && this.lockedDate.isBefore(LocalDateTime.now().minusMinutes(3));
    }

    @Getter
    public enum Status {
        NORMAL("정상"), LOCKED("잠김"), WITHDRAWAL("탈퇴");
        Status(String kor) {
            this.value = kor;
        }
        private final String value;
    }

    public static MyMember fromByPk(Long myMemberId) {
        MyMember myMember = new MyMember();
        myMember.myMemberId = myMemberId;
        return myMember;
    }
}
