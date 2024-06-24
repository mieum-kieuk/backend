package archivegarden.shop.dto.community.inquiry;

import archivegarden.shop.entity.Product;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class ProductPopupDto {

    private Long id;
    private String name;
    private String displayImage;
    private String price;

    @QueryProjection
    public ProductPopupDto(Long id, String name, int price, String displayImage) {
        this.id = id;
        this.name = name;
        this.price = new DecimalFormat("###,###원").format(price);
        this.displayImage = displayImage;
    }
}
