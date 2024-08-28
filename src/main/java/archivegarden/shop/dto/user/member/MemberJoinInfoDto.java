package archivegarden.shop.dto.user.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberJoinInfoDto {

    private String loginId;
    private String name;
    private String email;

    public MemberJoinInfoDto(String loginId, String name, String email) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }
}
