package archivegarden.shop.dto.user.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerificationCodeRequestDto {

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
    private String verificationCode;

    public String getPhonenumber() {
        return String.join("-", this.phonenumber1, this.phonenumber2, this.phonenumber3);
    }
}
