package archivegarden.shop.dto.admin.product.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminProductListDto {

    private Long id;
    private String name;
    private String categoryName;
    private int stockQuantity;
    private String price;
    private String salePrice;
    private String displayImageData;

    public AdminProductListDto(Product product, List<AdminProductImageDto> productImageDtos) {
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
        this.displayImageData = productImageDtos.get(0).getImageData();
    }
}
