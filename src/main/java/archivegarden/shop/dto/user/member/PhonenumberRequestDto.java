package archivegarden.shop.dto.user.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhonenumberRequestDto {

    @NotBlank
    @Pattern(regexp = "^01(0|1|[6-9])$")
    private String phonenumber1;

    @NotBlank
    @Pattern(regexp = "^(\\d){3,4}$")
    private String phonenumber2;

    @NotBlank
    @Pattern(regexp = "^(\\d){4}")
    private String phonenumber3;

    public String getFormattedPhonenumber() {
        return this.phonenumber1 + "-" + this.phonenumber2 + "-" + this.phonenumber3;
    }
}
