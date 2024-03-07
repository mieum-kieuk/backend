package archivegarden.shop.dto.member;

import archivegarden.shop.entity.FindAccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindIdForm {

    private FindAccountType findType;
    private String name;
    private String email;
    private String phonenumber1;
    private String phonenumber2;
    private String phonenumber3;
}
