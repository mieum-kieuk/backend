package archivegarden.shop.dto.user.community.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddInquiryForm {

    @NotNull(message = "상품을 선택해 주세요.")
    private Long productId;

    @NotBlank(message = "제목을 작성해 주세요.")
    private String title;

    @NotBlank(message = "내용을 작성해 주세요.")
    private String content;

    private Boolean isSecret;
}
