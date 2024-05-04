package archivegarden.shop.entity;

import archivegarden.shop.dto.member.MemberSaveForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", length = 20, nullable = false)
    private String loginId;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 12, nullable = false)
    private String name;

    @Column(name = "phone_number", length = 11, nullable = false)
    private String phonenumber;

    @Column(length = 45, nullable = false)
    private String email;

    @Column(length = 10, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Grade grade;

    @Column(length = 30, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(length = 10, nullable = false)
    private String agree_to_receive_sms;

    @Column(length = 10, nullable = false)
    private String agree_to_receive_mail;

    @Column(length = 10, nullable = false)
    private String isEmailVerified;

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<ShippingAddress> shippingAddressList = new ArrayList<>();

    //==비즈니스 로직==//
    /**
     * 이메일 인증 완료
     */
    public void completeEmailVerification() {
        this.isEmailVerified = Boolean.toString(true).toUpperCase();
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    //==생성자 메서드==//
    public static Member createMember(MemberSaveForm form) {
        Member member = new Member();
        member.loginId = form.getLoginId();
        member.password = form.getPassword();
        member.name = form.getName();
        member.phonenumber = form.getPhonenumber1() + "-" + form.getPhonenumber2() + "-" + form.getPhonenumber3();
        member.email = form.getEmail();
        member.grade = Grade.WHITE;
        member.authority = Authority.ROLE_USER;
        member.agree_to_receive_sms = Boolean.toString(form.isAgree_to_receive_sms()).toUpperCase();
        member.agree_to_receive_mail = Boolean.toString(form.isAgree_to_receive_mail()).toUpperCase();
        member.isEmailVerified = Boolean.toString(false).toUpperCase();
        return member;
    }
}
