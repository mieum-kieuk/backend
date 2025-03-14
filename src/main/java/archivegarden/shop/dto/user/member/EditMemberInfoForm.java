package archivegarden.shop.dto.user.member;

import archivegarden.shop.entity.Member;
import lombok.Getter;

@Getter
public class EditMemberInfoForm {

    private String loginId;
    private String name;
    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
    private String email;
    private boolean isEmailVerified;
    private boolean agreeToReceiveSms;
    private boolean agreeToReceiveEmail;

    public EditMemberInfoForm(Member member) {
        this.loginId = member.getLoginId();
        this.name = member.getName();
        String[] phonenumber = member.getPhonenumber().split("-");
        this.phonenumber1 = phonenumber[0];
        this.phonenumber2 = phonenumber[1];
        this.phonenumber3 = phonenumber[2];
        this.email = member.getEmail();
        this.isEmailVerified = member.isEmailVerified();
        this.agreeToReceiveSms = member.isAgreeToReceiveSms();
        this.agreeToReceiveEmail = member.isAgreeTotReceiveEmail();
    }
}
