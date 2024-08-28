package archivegarden.shop.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinCompletionInfoDto {

    private String loginId;
    private String name;
    private String email;

    public JoinCompletionInfoDto(String loginId, String name, String email) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }
}
