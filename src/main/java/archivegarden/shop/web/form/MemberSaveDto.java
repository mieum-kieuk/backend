package archivegarden.shop.web.form;

import archivegarden.shop.entity.Authority;
import archivegarden.shop.entity.Grade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSaveDto {

    private String loginId;
    private String password;
    private String name;
    private String phonenumber;
    private String email;
    private Grade grade;
    private Authority authority;
    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_email;

    public MemberSaveDto(MemberSaveForm form) {
        this.loginId = form.getLoginId();
        this.password = form.getPassword();
        this.name = form.getName();
        this.phonenumber = form.getPhonenumber();
        this.email = form.getEmail();
        this.grade = Grade.GREEN;
        this.authority = Authority.ROLE_USER;
        this.agree_to_receive_sms = form.isAgree_to_receive_sms();
        this.agree_to_receive_email = form.isAgree_to_receive_email();
    }
}
