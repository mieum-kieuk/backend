package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {

    private Long id;
    private String storeImageName;

    public ProductImageDto(ProductImage image) {
        id = image.getId();
        storeImageName = image.getStoreImageName();
    }
}
