package archivegarden.shop.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequestDto {

    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
    private String verificationNo;
}
