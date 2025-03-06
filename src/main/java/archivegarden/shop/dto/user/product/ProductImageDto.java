package archivegarden.shop.dto.user.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;

@Getter
public class ProductImageDto {

    private Long id;
    private ImageType imageType;
    private String imageData;

    public ProductImageDto(ProductImage productImage, String encodedImageData) {
        this.id = productImage.getId();
        this.imageType = productImage.getImageType();
        this.imageData = encodedImageData;
    }
}

