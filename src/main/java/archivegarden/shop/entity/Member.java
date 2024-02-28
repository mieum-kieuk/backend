package archivegarden.shop.entity;

import archivegarden.shop.dto.member.MemberSaveDto;
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

    @Column(name = "login_id")
    private String loginId;

    private String password;
    private String name;

    @Column(name = "phone_number")
    private String phonenumber;

    private String email;

    @Enumerated(value = EnumType.STRING)
    private Grade grade;

    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    private String agree_to_receive_sms;
    private String agree_to_receive_mail;
    private String isEmailVerified;

    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<ShippingAddress> shippingAddressList = new ArrayList<>();

    public Member(MemberSaveDto dto) {
        this.loginId = dto.getLoginId();
        this.password = dto.getPassword();
        this.name = dto.getName();
        this.phonenumber = dto.getPhonenumber();
        this.email = dto.getEmail();
        this.grade = Grade.WHITE;
        this.authority = Authority.ROLE_USER;
        this.agree_to_receive_sms = Boolean.toString(dto.isAgree_to_receive_sms()).toUpperCase();
        this.agree_to_receive_mail = Boolean.toString(dto.isAgree_to_receive_mail()).toUpperCase();
        this.isEmailVerified = Boolean.toString(false).toUpperCase();
    }
}
