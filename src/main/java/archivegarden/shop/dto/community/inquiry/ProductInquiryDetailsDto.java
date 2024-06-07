package archivegarden.shop.dto.community.inquiry;

import archivegarden.shop.entity.ProductInquiry;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ProductInquiryDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String writer;
    private String writerLoginId;
    private Long productId;
    private String productName;
    private String productPrice;
    private String productImage;

    @QueryProjection
    public ProductInquiryDetailsDto(ProductInquiry inquiry, String writer, String writerLoginId,
                                    Long productId, String productName, int productPrice, String productImage) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(inquiry.getCreatedAt());
        this.writer = writer.substring(0, 1) + "****";
        this.writerLoginId = writerLoginId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = new DecimalFormat("###,###원").format(productPrice);
        this.productImage = productImage;

    }
}
