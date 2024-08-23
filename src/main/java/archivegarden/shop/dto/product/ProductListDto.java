package archivegarden.shop.dto.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListDto {

    private Long id;
    private String name;
    private String price;
    private boolean isDiscounted;
    private Integer discountPercent;
    private String salePrice;
    private boolean isSoldOut;
    private List<String> displayImageUrls;

    public ProductListDto(Product product, List<String> displayImageUrls) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());
        Discount discount = product.getDiscount();
        if (discount != null) {
            this.isDiscounted = true;
            this.discountPercent = discount.getDiscountPercent();
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###원").format(product.getPrice() - discountAmount);
        }

        if (product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }

        this.displayImageUrls = displayImageUrls;
    }
}
