package archivegarden.shop.dto.admin.product.inquiry;

import archivegarden.shop.entity.ProductInquiry;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Getter
public class ProductInquiryAdminDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String writer;
    private Long productId;
    private String productName;
    private String productPrice;
    private String productImage;

    @QueryProjection
    public ProductInquiryAdminDetailsDto(ProductInquiry inquiry, String name, Long productId, String productName, int productPrice, String productImage) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(inquiry.getCreatedAt());
        this.writer = name;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = new DecimalFormat("###,###원").format(productPrice);
        this.productImage = productImage;
    }
}
