package archivegarden.shop.dto.admin.admins;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAdminForm {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}+", message = "5~20자의 영문 소문자, 숫자 조합을 사용해 주세요.")
    private String loginId;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W)[a-zA-Z\\d\\W]{8,16}+$", message = "8~20자의 영문 대/소문자, 숫자, 특수문자 조합을 사용해 주세요.")
    private String password;
    private String passwordConfirm;

    @Pattern(regexp = "^[가-힣]{2,5}$", message = "2~5자의 한글을 사용해 주세요. (특수기호, 공백 사용 불가)")
    private String name;

    @Pattern(regexp = "^[a-zA-Z\\d]([-_.]?[a-zA-Z\\d])*@[a-zA-Z\\d]*\\.[a-zA-Z]{2,3}$", message = "유효한 이메일을 입력해 주세요.")
    private String email;
}
