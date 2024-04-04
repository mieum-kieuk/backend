package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {

    private Long id;
    private String uploadImageName;
    private String storeImageName;
    private ImageType imageType;

    public ProductImageDto(ProductImage image) {
        id = image.getId();
        uploadImageName = image.getUploadImageName();
        storeImageName = image.getStoreImageName();
        imageType = image.getImageType();
    }
}
