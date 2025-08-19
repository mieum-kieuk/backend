package archivegarden.shop.entity;

import archivegarden.shop.dto.user.member.JoinMemberForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(name = "agree_to_receive_sms", nullable = false)
    private boolean agreeToReceiveSms;

    @Column(name = "agree_to_receive_mail", nullable = false)
    private boolean agreeTotReceiveEmail;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Membership membership;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Delivery> deliveries = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<SavedPoint> savedPoints = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Wish> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public static Member createMember(JoinMemberForm form, Membership membership) {
        Member member = new Member();
        member.membership = membership;
        member.loginId = form.getLoginId();
        member.password = form.getPassword();
        member.name = form.getName();
        member.phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        member.email = form.getEmail();
        member.authority = Authority.ROLE_USER;
        member.agreeToReceiveSms = form.isAgreeToReceiveSms();
        member.agreeTotReceiveEmail = form.isAgreeToReceiveEmail();
        member.isEmailVerified = false;
        return member;
    }

    /**
     * 배송지 추가
     *
     * @param delivery 추가할 배송지
     */
    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
        delivery.setMember(this);
    }
    /**
     * 이메일 인증 상태 수정
     */
    public void updateEmailVerificationStatus(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 이메일 변경
     */
    public void updateEmail(String email) {
        this.email = email;
    }
}
