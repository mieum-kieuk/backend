package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {

    private Long id;
    private String storeImageName;
    private String uploadImageName;

    public ProductImageDto(ProductImage image) {
        this.id = image.getId();
        this.storeImageName = image.getStoreImageName();
        this.uploadImageName = image.getUploadImageName();
    }
}
