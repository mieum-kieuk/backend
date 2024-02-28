package archivegarden.shop.dto.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveForm {

    @Pattern(regexp = "(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}+$", message = "5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.")
    private String loginId;

    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*\\d)(?=.*\\W)[a-zA-Z\\d\\W]{8,16}$", message = "8~16자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.")
    private String password;

    private String passwordConfirm;

    @Pattern(regexp = "^[a-zA-z가-힣]{1,12}$", message = "한글, 영문 대/소문자를 사용해 주세요. (특수기호, 공백 사용 불가)")
    private String name;

    private String zipCode;     //우편번호
    private String basicAddress;    //기본주소
    private String detailAddress;    //상세주소

    @Pattern(regexp = "^01(0|1|[6-9])$")
    private String phonenumber1;

    @Pattern(regexp = "^(\\d){3,4}$")
    private String phonenumber2;

    @Pattern(regexp = "^(\\d){4}$")
    private String phonenumber3;

    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", message = "유효한 이메일을 입력해 주세요.")
    private String email;

    @AssertTrue(message = "이용약관에 동의해 주세요.")
    private boolean agree_to_terms_of_use;

    @AssertTrue(message = "개인정보 수집 및 이용 방침에 동의해 주세요.")
    private boolean agree_to_personal_information;

    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_mail;
}
