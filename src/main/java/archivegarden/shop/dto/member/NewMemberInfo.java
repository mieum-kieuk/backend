package archivegarden.shop.dto.member;

import archivegarden.shop.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewMemberInfo {

    private String loginId;
    private String name;
    private String email;

    public NewMemberInfo(Member member) {
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
