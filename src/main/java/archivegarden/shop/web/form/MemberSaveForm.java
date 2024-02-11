package archivegarden.shop.web.form;

import archivegarden.shop.web.validation.ValidationGroups.NotBlankGroup;
import archivegarden.shop.web.validation.ValidationGroups.PatternGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberSaveForm {

    @NotBlank(message = "아이디를 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*\\d)[a-zA-Z\\d]{6,16}+$", message = "6자 이상 16자 이하의 영문 혹은 영문과 숫자를 조합", groups = PatternGroup.class)
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*\\d)(?=.*\\W)[a-zA-Z\\d\\W]{10,16}$", message = "10자 이상 16자 이하의 영문, 숫자, 특수문자 조합", groups = PatternGroup.class)
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*\\d)(?=.*\\W)[a-zA-Z\\d\\W]{10,16}$", message = "10자 이상 16자 이하의 영문, 숫자, 특수문자 조합", groups = PatternGroup.class)
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[가-힣]{2,5}$", message = "공백없이 한글(2~5자)")
    private String name;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")
    private String phonenumber;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;

    @NotNull(message = "이용약관에 동의하세요.")
    private boolean agree_to_terms_of_use;

    @NotNull(message = "개인정보 수집 및 이용 방침에 동의하세요")
    private boolean agree_to_personal_information;

    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_email;
}
