package archivegarden.shop.dto.user.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindAccountResponseDto {

    private String code;
    private String message;
    private String token;
}
