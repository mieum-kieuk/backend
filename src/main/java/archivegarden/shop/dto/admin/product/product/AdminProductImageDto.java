package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;

@Getter
public class AdminProductImageDto {

    private Long id;
    private ImageType imageType;
    private String imageName;
    private String imageData;

    public AdminProductImageDto(ProductImage productImage, String encodedImageData) {
        this.id = productImage.getId();
        this.imageType = productImage.getImageType();
        this.imageName = productImage.getImageName();
        this.imageData = encodedImageData;
    }
}
