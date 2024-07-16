package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListDto {

    private Long id;
    private String name;
    private String categoryName;
    private int stockQuantity;
    private int price;
    private int salesPrice;
    private String displayImage;

    public ProductListDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryName = product.getCategory().getDisplayName();
        this.stockQuantity = product.getStockQuantity();
        this.price = product.getPrice();
        Discount discount = product.getDiscount();
        if(discount != null) {
            int discountPercent = discount.getDiscountPercent();
            this.salesPrice = this.price - this.price * discountPercent / 100;
        } else {
            this.salesPrice = this.price;
        }
        this.displayImage = product.getProductImages().get(0).getStoreImageName();
    }
}
