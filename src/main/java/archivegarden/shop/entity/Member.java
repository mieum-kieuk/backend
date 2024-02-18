package archivegarden.shop.entity;

import archivegarden.shop.web.form.MemberSaveDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String phonenumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private boolean isEmailVerified;
    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_email;

    public Member(MemberSaveDto dto) {
        this.loginId = dto.getLoginId();
        this.password = dto.getPassword();
        this.name = dto.getName();
        this.phonenumber = dto.getPhonenumber();
        this.email = dto.getEmail();
        this.grade = Grade.GREEN;
        this.authority = Authority.ROLE_USER;
        this.isEmailVerified = false;
        this.agree_to_receive_sms = dto.isAgree_to_receive_sms();
        this.agree_to_receive_email = dto.isAgree_to_receive_email();
    }
}
