package archivegarden.shop.dto.user.community.inquiry;

import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductInquiry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
@NoArgsConstructor
public class EditProductInquiryForm {

    @NotBlank(message = "제목을 작성해 주세요.")
    private String title;

    @NotBlank(message = "내용을 작성해 주세요.")
    private String content;

    private boolean isSecret;

    @NotNull(message = "상품을 선택해 주세요.")
    private Long productId;

    private String productName;
    private String productPrice;
    private String productImage;

    public EditProductInquiryForm(ProductInquiry inquiry) {
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
//        this.isSecret = inquiry.isSecret();
        Product product = inquiry.getProduct();
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = new DecimalFormat("###,###원").format(product.getPrice());
//        List<ProductImage> images = product.getImages();
//        for (ProductImage image : images) {
//            if(image.getImageType() == ImageType.DISPLAY) {
//                this.productImage = image.getStoreImageName();
//            }
//        }
    }
}
