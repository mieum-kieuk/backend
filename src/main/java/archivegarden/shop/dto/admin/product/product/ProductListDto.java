package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListDto {

    private Long id;
    private String name;
    private String categoryName;
    private int stockQuantity;
    private String price;
    private String salePrice;
    private String displayImageUrl;

    public ProductListDto(Product product, String downloadDisplayImage) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryName = product.getCategory().getDisplayName();
        this.stockQuantity = product.getStockQuantity();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());
        Discount discount = product.getDiscount();
        if(discount != null) {
            double salePriceDouble = product.getPrice() - (double) product.getPrice() * discount.getDiscountPercent() / 100;
            this.salePrice = new DecimalFormat("###.###원").format(Math.round(salePriceDouble));
        } else {
            this.salePrice = this.price;
        }
        this.displayImageUrl = downloadDisplayImage;
    }
}
