package archivegarden.shop.dto.community.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ProductInquiryListDto {

    private Long id;
    private String title;
    private Boolean isSecret;
    private String isAnswered;
    private String createdAt;
    private Long productId;
    private String productImage;
    private String writer;
    private String writerLoginId;

    @QueryProjection
    public ProductInquiryListDto(Long productInqueryId, String title, String isSecret, String isAnswered, LocalDateTime createdAt,
                             String writer, String writerLoginId, Long productId, String productImage) {
        this.id = productInqueryId;
        this.productId = productId;
        this.productImage = productImage;
        this.title = title;
        this.writer = writer.substring(0, 1) + "****";
        this.writerLoginId = writerLoginId;
        this.isSecret = Boolean.parseBoolean(isSecret);
        this.isAnswered = Boolean.parseBoolean(isAnswered) ? "답변완료" : "답변대기";
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(createdAt);
    }
}
