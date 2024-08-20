package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;

@Getter
public class ProductImageDto {

    private Long id;
    private ImageType imageType;
    private String imageName;
    private String imageUrl;

    public ProductImageDto(ProductImage productImage, String imageUrl) {
        this.id = productImage.getId();
        this.imageType = productImage.getImageType();
        this.imageName = extractFileName(productImage.getImageUrl());
        this.imageUrl = imageUrl;
    }

    private String extractFileName(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return url;
        }
        return url.substring(lastSlashIndex + 38);
    }
}
