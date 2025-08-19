package archivegarden.shop.dto.user.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindPasswordByEmailRequestDto(

        @NotBlank(message = "아이디를 입력해 주세요.")
        String loginId,

        @NotBlank(message = "이름을 입력해 주세요.")
        String name,

        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "이메일 형식으로 입력해 주세요.")
        String email
) {}
