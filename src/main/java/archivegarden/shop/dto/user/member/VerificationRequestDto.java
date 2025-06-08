package archivegarden.shop.dto.user.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequestDto {

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
    private String verificationNo;

    public String getFormattedPhonenumber() {
        return this.phonenumber1 + "-" + this.phonenumber2 + "-" + this.phonenumber3;
    }
}
