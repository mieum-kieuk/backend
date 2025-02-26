package archivegarden.shop.dto.admin.product.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class AdminProductSummaryDto {

    private Long id;
    private String name;
    private String price;
    private String displayImageData;

    @QueryProjection
    public AdminProductSummaryDto(Long id, String name, int price, String displayImageData) {
        this.id = id;
        this.name = name;
        this.price = new DecimalFormat("###,###원").format(price);
        this.displayImageData = displayImageData;
    }
}
