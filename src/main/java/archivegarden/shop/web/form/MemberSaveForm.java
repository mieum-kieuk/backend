package archivegarden.shop.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberSaveForm {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    private String phonenumber;

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotNull(message = "이용약관에 동의하세요.")
    private boolean agree_to_terms_of_use;

    @NotNull(message = "개인정보 수집 및 이용 방침에 동의하세요")
    private boolean agree_to_personal_information;

    private boolean agree_to_receive_sms;
    private boolean agree_to_receive_email;
}
