package archivegarden.shop.dto.user.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class ProductSummaryDto {

    private Long id;
    private String name;
    private String price;
    private String displayImageData;

    @QueryProjection
    public ProductSummaryDto(Long id, String name, int price, String displayImageData) {
        this.id = id;
        this.name = name;
        this.price = new DecimalFormat("###,###원").format(price);
        this.displayImageData = displayImageData;
    }
}
