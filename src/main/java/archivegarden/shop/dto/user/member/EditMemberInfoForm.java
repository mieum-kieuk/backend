package archivegarden.shop.dto.user.member;

import archivegarden.shop.entity.Address;
import archivegarden.shop.entity.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditMemberInfoForm {

    private String loginId;
    private String name;

    private String newPassword;
    private String newPasswordConfirm;

    private String zipCode;
    private String basicAddress;
    private String detailAddress;

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;

    private String email;
    private boolean isEmailVerified;
    private boolean agreeToReceiveSms;
    private boolean agreeToReceiveEmail;

    @QueryProjection
    public EditMemberInfoForm(Member member, Address address) {
        this.loginId = member.getLoginId();
        this.name = member.getName();

        this.zipCode = address.getZipCode();
        this.basicAddress = address.getBasicAddress();
        this.detailAddress = address.getDetailAddress();

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
