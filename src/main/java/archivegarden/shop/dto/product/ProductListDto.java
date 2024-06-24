package archivegarden.shop.dto.product;

import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductListDto {

    private Long id;
    private String name;
    private String price;   //정가
    private boolean isDiscounted;
    private Integer discountPercent;    //할인율
    private String salePrice;   //할인가
    private boolean isSoldOut;
    private List<String> displayImages;

    public ProductListDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = new DecimalFormat("###,###원").format(product.getPrice());

        Discount discount = product.getDiscount();
        if (discount != null) {
            this.isDiscounted = true;
            this.discountPercent = Integer.valueOf(discount.getDiscountPercent());
            int discountAmount = product.getPrice() * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###원").format(product.getPrice() - discountAmount);
        }

        if (product.getStockQuantity() <= 0) {
            this.isSoldOut = true;
        }

        this.displayImages = product.getImages()
                .stream()
                .map((image) -> image.getStoreImageName())
                .collect(Collectors.toList());
    }
}
