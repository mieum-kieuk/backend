package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
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
    private List<String> displayImages = new ArrayList<>();
    private List<String> detailsImages = new ArrayList<>();

    public ProductDetailsDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryName = product.getCategory().getDisplayName();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());
        this.discountName = product.getDiscount() != null ? "[" + product.getDiscount().getDiscountPercent() + "%] " + product.getDiscount().getName() : "할인이 적용되지 않았습니다.";
        this.stockQuantity = new DecimalFormat("###,###개").format(product.getStockQuantity());
        this.details = product.getDetails();
        this.sizeGuide = product.getSizeGuide();
        this.shipping = product.getShipping();
        this.notice = product.getNotice();
        product.getProductImages().forEach(image -> {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.displayImages.add(image.getStoreImageName());
            } else {
                this.detailsImages.add(image.getStoreImageName());
            }
        });
    }
}
