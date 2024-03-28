package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.Category;
import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDto {

    private Long id;
    private String name;
    private Category category;
    private String price;
    private String stockQuantity;
    private String details;
    private String sizeGuide;
    private String shipping;
    private String notice;
    private String displayImage1;
    private String displayImage2;
    private List<String> detailsImages = new ArrayList<>();

    public ProductDto(Product product) {
        id = product.getId();
        name = product.getName();
        category = product.getCategory();
        price = new DecimalFormat("###,###").format(product.getPrice());
        stockQuantity = new DecimalFormat("###,###").format(product.getStockQuantity());
        details = product.getDetails();
        sizeGuide = product.getSizeGuide();
        shipping = product.getShipping();
        notice = product.getNotice();

        List<ProductImage> images = product.getImages();
        for (ProductImage image : images) {
            if (image.getImageType() == ImageType.DISPLAY) {
                displayImage1 = image.getStoreImageName();
            } else if (image.getImageType() == ImageType.HOVER) {
                displayImage2 = image.getStoreImageName();
            } else {
                detailsImages.add(image.getStoreImageName());
            }
        }
    }
}
