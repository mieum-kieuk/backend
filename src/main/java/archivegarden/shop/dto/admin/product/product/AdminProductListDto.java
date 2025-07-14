package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminProductListDto {

    private Long id;
    private String name;
    private String categoryName;
    private int stockQuantity;
    private String price;
    private String salePrice;
    private String displayImage;

    public AdminProductListDto(Product product, AdminProductImageDto productImageDto) {
        this.id = product.getId();
        this.name = product.getName();
        this.categoryName = product.getCategory().getDisplayName();
        this.stockQuantity = product.getStockQuantity();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());
        Discount discount = product.getDiscount();
        if(discount != null && isDateBetween(discount.getStartedAt(), discount.getExpiredAt())) {
            double salePriceDouble = product.getPrice() - (double) product.getPrice() * discount.getDiscountPercent() / 100;
            this.salePrice = new DecimalFormat("###.###원").format(Math.round(salePriceDouble));
        } else {
            this.salePrice = this.price;
        }
        this.displayImage = productImageDto.getImageData();
    }

    private boolean isDateBetween(LocalDateTime startedAt, LocalDateTime expiredAt) {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(startedAt) || now.isEqual(startedAt)) && (now.isBefore(expiredAt) || now.isEqual(expiredAt));
    }
}
