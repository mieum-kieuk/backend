package archivegarden.shop.dto.user.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class PopupProductDto {

    private Long id;
    private String name;
    private String price;
    private String displayImageUrl;

    @QueryProjection
    public PopupProductDto(Long id, String name, int price, String displayImageUrl) {
        this.id = id;
        this.name = name;
        this.price = new DecimalFormat("###,###원").format(price);
        this.displayImageUrl = displayImageUrl;
    }
}
