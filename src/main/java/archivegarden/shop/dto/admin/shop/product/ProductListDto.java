package archivegarden.shop.dto.admin.shop.product;

import archivegarden.shop.entity.*;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class ProductListDto {

    private Long id;
    private String name;
    private String category;
    private String price;
    private String salePrice;
    private String stockQuantity;
    private String displayImage;

    @QueryProjection
    public ProductListDto(Long productId, String productName, Category category, int productPrice, int stockQuantity, Integer discountPercent, String displayImage) {
        this.id = productId;
        this.name = productName;
        this.category = category.getDisplayName();
        this.price = new DecimalFormat("###,###원").format(productPrice);
        this.salePrice = discountPercent != null ? new DecimalFormat("###,###원").format(discount(productPrice, discountPercent)) : this.price;
        this.stockQuantity = stockQuantity <= 0 ? "SOLD OUT" : new DecimalFormat("###,###").format(stockQuantity);
        this.displayImage = displayImage;
    }

    private int discount(int price, int discountPercent) {
        int discountPrice = price * discountPercent / 100;
        return price - discountPrice;
    }
}
