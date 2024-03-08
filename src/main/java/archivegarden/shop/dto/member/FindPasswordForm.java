package archivegarden.shop.dto.member;

import archivegarden.shop.entity.FindAccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindPasswordForm {

    public FindAccountType findType;
    public String loginId;
    public String name;
    public String email;
    public String phonenumber1;
    public String phonenumber2;
    public String phonenumber3;
}
