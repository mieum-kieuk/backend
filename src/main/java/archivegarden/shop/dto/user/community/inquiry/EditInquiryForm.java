package archivegarden.shop.dto.user.community.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
@NoArgsConstructor
public class EditInquiryForm {

    @NotBlank(message = "제목을 작성해 주세요.")
    private String title;

    @NotBlank(message = "내용을 작성해 주세요.")
    private String content;

    private Boolean isSecret;

    @NotNull(message = "상품을 선택해 주세요.")
    private Long productId;

    private String productName;
    private String productPrice;
    private String productImageData;

    @QueryProjection
    public EditInquiryForm(String title, String content, boolean isSecret, Long productId, String productName, int productPrice, String productImageUrl) {
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = new DecimalFormat("###,###원").format(productPrice);
        this.productImageData = productImageUrl;
    }
}
