package archivegarden.shop.dto.common;

import lombok.Getter;

@Getter
public class JoinSuccessDto {

    private String loginId;
    private String name;
    private String email;

    public JoinSuccessDto(String loginId, String name, String email) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }
}
