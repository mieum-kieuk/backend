package archivegarden.shop.dto.shop.product;

import archivegarden.shop.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class ProductListDto {

    private Long id;
    private String name;
    private String displayImage;
    private String hoverImage;
    private String price;
    private boolean isDiscounted;
    private int discountPercent;
    private String salePrice;
    private boolean isSoldOut;

    public ProductListDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.displayImage = null;
        this.hoverImage = null;
        for (ProductImage image : product.getImages()) {
            if (image.getImageType() == ImageType.DISPLAY) {
                this.displayImage = image.getStoreImageName();
            } else if (image.getImageType() == ImageType.HOVER) {
                this.hoverImage = image.getStoreImageName();
            }
        }

        this.price = new DecimalFormat("###,###").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount != null) {
            this.isDiscounted = true;
            this.discountPercent = discount.getDiscountPercent();
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###").format(product.getPrice() - discountAmount);
        }

        if(product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }
    }
}
