package archivegarden.shop.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewMemberInfo {

    private String loginId;
    private String name;
    private String email;

    public NewMemberInfo(String loginId, String name, String email) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }
}
