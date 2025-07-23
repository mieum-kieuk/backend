package archivegarden.shop.dto.user.order;

import archivegarden.shop.entity.Discount;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class CartListDto {

    private Long id;
    private String name;
    private String price;
    private int count;
    private String displayImage;
    private boolean isDiscounted;
    private boolean isSoldOut;
    private int discountPercent;
    private String salePrice;
    private String totalPrice;

    @QueryProjection
    public CartListDto(Long productId, String name, int price, int count, String displayImageData, Discount discount, int stockQuantity) {
        this.id = productId;
        this.name = name;
        this.count = count;
        this.displayImage = displayImageData;
        this.price = new DecimalFormat("###,###원").format(price);

        if (discount == null) { //할인 중인 상품 X
            this.salePrice = this.price;
            this.totalPrice = new DecimalFormat("###,###원").format(price * count);
        } else { //할인 중인 상품 O
            this.isDiscounted = Boolean.TRUE;
            this.discountPercent = discount.getDiscountPercent();
            int discountAmount = price * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###원").format(price - discountAmount);
            this.totalPrice = new DecimalFormat("###,###원").format((price - discountAmount) * count);
        }

        if (stockQuantity <= 0) {
            this.isSoldOut = Boolean.TRUE;
        }
    }
}
