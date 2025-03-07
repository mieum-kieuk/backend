package archivegarden.shop.dto.user.wish;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class MyWishDto {

    private Long productId;
    private String name;
    private String price;
    private boolean isDiscounted;
    private String discountPercent;
    private String salePrice;
    private boolean isSoldOut;
    private String displayImageData;

    @QueryProjection
    public MyWishDto(Long productId, String name, int price, int stockQuantity, Integer discountPercent, String displayImageData) {
        this.productId = productId;
        this.name = name;
        this.price = new DecimalFormat("###.###원").format(price);
        if(discountPercent != null) {
            this.isDiscounted = true;
            this.discountPercent = discountPercent + "%";
            int discountAmount = price * discountPercent / 100;
            this.salePrice = new DecimalFormat("###.###원").format(price - discountAmount);
        } else {
            this.salePrice = this.price;
        }

        if(stockQuantity <= 0) {
            this.isSoldOut = true;
        }

        this.displayImageData = displayImageData;
    }
}
