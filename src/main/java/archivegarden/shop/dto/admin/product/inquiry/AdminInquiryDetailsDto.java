package archivegarden.shop.dto.admin.product.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminInquiryDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String writerLoginId;
    private Long productId;
    private String productName;
    private String productPrice;
    private String productDisplayImage;

    @QueryProjection
    public AdminInquiryDetailsDto(Long id, String title, String content, LocalDateTime createdAt, String writerLoginId, Long productId, String productName, int productPrice, String productDisplayImage) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(createdAt);
        this.writerLoginId = writerLoginId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = new DecimalFormat("###,###원").format(productPrice);
        this.productDisplayImage = productDisplayImage;
    }
}
