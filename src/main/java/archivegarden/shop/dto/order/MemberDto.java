package archivegarden.shop.dto.order;

import archivegarden.shop.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {

    private Long id;
    private String name;
    private String phonenumber;
    private String email;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.phonenumber = member.getPhonenumber();
        this.email = member.getEmail();
    }
}
