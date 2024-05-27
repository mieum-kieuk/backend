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
    private String displayImage;
    private String hoverImage;
    private List<String> detailsImages = new ArrayList<>();

    public ProductDetailsDto(Product product) {
        id = product.getId();
        name = product.getName();
        categoryName = product.getCategory().getDisplayName();
        price = new DecimalFormat("###,###원").format(product.getPrice());

        discountName = product.getDiscount() != null ? "[" + product.getDiscount().getDiscountPercent() + "%] " + product.getDiscount().getName() : "할인 혜택이 없습니다.";
        stockQuantity = new DecimalFormat("###,###개").format(product.getStockQuantity());
        details = product.getDetails();
        sizeGuide = product.getSizeGuide();
        shipping = product.getShipping();
        notice = product.getNotice();

        List<ProductImage> images = product.getImages();
        for (ProductImage image : images) {
            if (image.getImageType() == ImageType.DISPLAY) {
                displayImage = image.getStoreImageName();
            } else if (image.getImageType() == ImageType.HOVER) {
                hoverImage = image.getStoreImageName();
            } else {
                detailsImages.add(image.getStoreImageName());
            }
        }
    }
}
