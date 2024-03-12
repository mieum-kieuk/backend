package archivegarden.shop.dto.member;

import archivegarden.shop.entity.FindAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordDto {

    private String findType;    //email, sms
    private String description; //이메일, 휴대전화번호
    private String value;

    public FindPasswordDto(FindAccountType findAccountType, String value) {
        this.findType = findAccountType.name().toLowerCase();
        this.description = findAccountType.getDescription();
        this.value = value;
    }
}


