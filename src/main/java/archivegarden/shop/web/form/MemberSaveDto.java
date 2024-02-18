package archivegarden.shop.web.form;

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
    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_email;

    public MemberSaveDto(MemberSaveForm form) {
        this.loginId = form.getLoginId();
        this.password = form.getPassword();
        this.name = form.getName();
        this.phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        this.email = form.getEmail();
        this.agree_to_receive_sms = form.isAgree_to_receive_sms();
        this.agree_to_receive_email = form.isAgree_to_receive_email();
    }
}
