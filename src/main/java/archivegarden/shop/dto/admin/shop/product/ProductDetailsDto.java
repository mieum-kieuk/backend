package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDetailsDto {

    private Long id;
    private String name;
    private String categoryName;
    private String price;
    private String discountName;
    private String stockQuantity;
    private String details;
    private String sizeGuide;
    private String shipping;
    private String notice;
    private String displayImage1;
    private String displayImage2;
    private List<String> detailsImages = new ArrayList<>();

    public ProductDetailsDto(Product product) {
        id = product.getId();
        name = product.getName();
        categoryName = product.getCategory().getDisplayName();
        price = new DecimalFormat("###,###").format(product.getPrice());
        discountName = "[" + product.getDiscount().getDiscountPercent() + "%] " + product.getDiscount().getName();
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
