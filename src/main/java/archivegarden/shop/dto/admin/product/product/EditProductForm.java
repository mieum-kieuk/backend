package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditProductForm {

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

    private MultipartFile displayImage;
    private MultipartFile hoverImage;
    private List<MultipartFile> detailsImages;
    private boolean hoverImageDeleted;
    private List<String> deleteDetailsImages = new ArrayList<>();

    private ProductImageDto originDisplayImage;
    private ProductImageDto originHoverImage;
    private List<ProductImageDto> originDetailsImages = new ArrayList<>();

    public EditProductForm(Product product) {
        this.name = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.details = product.getDetails();
        this.sizeGuide = product.getSizeGuide();
        this.shipping = product.getShipping();
        this.notice = product.getNotice();
        product.getProductImages().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.originDisplayImage = new ProductImageDto(image);
            } else if (image.getImageType() == ImageType.HOVER) {
                this.originHoverImage = new ProductImageDto(image);
            } else {
                this.originDetailsImages.add(new ProductImageDto(image));
            }
        });
    }
}
