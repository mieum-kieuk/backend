package archivegarden.shop.dto.admin.shop.product;

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

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditProductForm {

    private Long id;

    @NotBlank(message = "상품명을 입력해 주세요.")
    private String name;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Category category;

    @NotNull(message = "가격을 입력해 주세요.")
    @Positive(message = "유효한 값을 입력해 주세요.")
    private Integer price;

    private Long discountId;

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

    private String displayImage1;
    private String displayImage2;
    private List<String> detailsImages = new ArrayList<>();

    public EditProductForm(Product product) {
        id = product.getId();
        name = product.getName();
        category = product.getCategory();
        price = product.getPrice();
        discountId = product.getDiscount().getId();
        stockQuantity = product.getStockQuantity();
        details = product.getDetails();
        sizeGuide = product.getSizeGuide();
        shipping = product.getShipping();
        notice = product.getNotice();
        product.getImages().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                displayImage1 = image.getStoreImageName();
            } else if (image.getImageType() == ImageType.HOVER) {
                displayImage2 = image.getStoreImageName();
            } else {
                detailsImages.add(image.getStoreImageName());
            }
        });
    }
}
