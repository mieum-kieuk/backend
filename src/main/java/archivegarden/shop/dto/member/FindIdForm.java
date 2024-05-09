package archivegarden.shop.dto.member;

import archivegarden.shop.entity.FindAccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindIdForm {

    private FindAccountType findType;
    private String name;
    private String email;
    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
}
