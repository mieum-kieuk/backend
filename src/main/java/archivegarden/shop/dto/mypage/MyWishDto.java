package archivegarden.shop.dto.mypage;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class MyWishDto {

    Long wishId;
    Long productId;
    String name;
    String price;
    boolean isDiscounted;
    Integer discountPercent;
    String salePrice;
    String displayImage;
    boolean isSoldOut;

    @QueryProjection
    public MyWishDto(Long wishId, Long productId, String name, int price, int stockQuantity, Integer discountPercent, String displayImage) {
        this.wishId = wishId;
        this.productId = productId;
        this.name = name;
        this.price = new DecimalFormat("###.###원").format(price);
        this.discountPercent = discountPercent;
        if(discountPercent != null) {
            this.isDiscounted = true;
            int discountAmount = price * discountPercent / 100;
            this.salePrice = new DecimalFormat("###.###원").format(price - discountAmount);
        } else {
            this.salePrice = this.price;
        }

        if(stockQuantity <= 0) {
            this.isSoldOut = true;
        }

        this.displayImage = displayImage;
    }
}
