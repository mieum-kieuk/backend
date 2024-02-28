package archivegarden.shop.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class MemberSaveDto {

    private String loginId;
    private String password;
    private String name;
    private String address;
    private String phonenumber;
    private String email;
    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_mail;

    public MemberSaveDto(MemberSaveForm form) {
        this.loginId = form.getLoginId();
        this.password = form.getPassword();
        this.name = form.getName();
        this.address = fullAddress(form);
        this.phonenumber = form.getPhonenumber1() + form.getPhonenumber2() + form.getPhonenumber3();
        this.email = form.getEmail();
        this.agree_to_receive_sms = form.isAgree_to_receive_sms();
        this.agree_to_receive_mail = form.isAgree_to_receive_mail();
    }

    private String fullAddress(MemberSaveForm form) {
        String result = "";
        if(StringUtils.hasText(form.getZipCode()) && StringUtils.hasText(form.getBasicAddress())) {
            result = form.getZipCode() + " " + form.getBasicAddress();
            if (StringUtils.hasText(form.getDetailAddress())) {
                result = result + " " + form.getDetailAddress();
            }
        }

        return result;
    }
}
