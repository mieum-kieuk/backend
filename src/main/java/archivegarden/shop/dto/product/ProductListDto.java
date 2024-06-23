package archivegarden.shop.dto.shop.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;

@Getter
@Setter
public class ProductListDto {

    private Long id;
    private String name;
    private String price;   //정가
    private boolean isDiscounted;
    private Integer discountPercent;    //할인율
    private String salePrice;   //할인가
    private boolean isSoldOut;
    private String displayImage;
    private String hoverImage;

    @QueryProjection
    public ProductListDto(Long id, String name, int price, Integer discountPercent, int stockQuantity, List<String> displayImages) {
        this.id = id;
        this.name = name;
        this.price = new DecimalFormat("###,###원").format(price);

        if (discountPercent != null) {
            this.isDiscounted = true;
            int discountAmount = price * discountPercent / 100;
            this.salePrice = new DecimalFormat("###,###원").format(price - discountAmount);
        }

        if (stockQuantity <= 0) {
            this.isSoldOut = true;
        }

        this.displayImage = images.get(0);
        this.hoverImage = images.get(1);

    }
}
