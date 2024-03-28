package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaveForm {

    @NotBlank(message = "상품명을 입력해 주세요.")
    private String name;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Category category;

    @NotNull(message = "가격을 입력해 주세요.")
    @Positive(message = "유효한 값을 입력해 주세요.")
    private Integer price;

    @NotNull(message = "재고를 입력해 주세요.")
    @PositiveOrZero(message = "0 이상의 값을 입력해 주세요.")
    private Integer stockQuantity;

    @NotBlank(message = "상품 상세정보를 입력해 주세요.")
    private String details;

    @NotBlank(message = "상품 크기를 입력해 주세요.")
    private String sizeGuide;

    @NotBlank(message = "배송 정보를 입력해 주세요.")
    private String shipping;

    @NotBlank(message = "주의 사항을 입력해 주세요.")
    private String notice;

    @NotNull(message = "상품 목록에 보일 이미지를 첨부해 주세요.")
    private MultipartFile displayImage1;

    private MultipartFile displayImage2;

    private List<MultipartFile> detailsImages;
}
