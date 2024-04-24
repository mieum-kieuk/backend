package archivegarden.shop.dto.community.qna;

import archivegarden.shop.entity.Product;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class QnaPopupDto {

    private Long id;
    private String name;
    private String displayImage;
    private String price;

    public QnaPopupDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.displayImage = product.getImages().get(0).getStoreImageName();
        this.price = new DecimalFormat("###,###").format(product.getPrice());
    }
}
