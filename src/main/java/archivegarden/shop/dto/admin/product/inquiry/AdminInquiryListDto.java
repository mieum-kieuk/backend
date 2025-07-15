package archivegarden.shop.dto.admin.product.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminInquiryListDto {

    private Long id;
    private String title;
    private String createdAt;
    private Boolean isAnswered;
    private String writerLoginId;
    private Long productId;
    private String productDisplayImage;

    @QueryProjection
    public AdminInquiryListDto(Long id, String title, LocalDateTime createdAt, boolean isAnswered, String writerLoginId, Long productId, String productDisplayImage) {
        this.id = id;
        this.title = title;
        this.writerLoginId = writerLoginId;
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(createdAt);
        this.isAnswered = isAnswered;
        this.productId = productId;
        this.productDisplayImage = productDisplayImage;
    }
}

