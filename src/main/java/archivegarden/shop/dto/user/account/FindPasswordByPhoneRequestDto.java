package archivegarden.shop.dto.user.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FindPasswordByPhoneRequestDto(

        @NotBlank(message = "아이디를 입력해 주세요.")
        String loginId,

        @NotBlank(message = "이름을 입력해 주세요.")
        String name,

        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대전화번호 형식으로 입력해 주세요.")
        String phonenumber
) {}
