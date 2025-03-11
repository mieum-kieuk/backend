package archivegarden.shop.dto.user.community.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryListDto {

    private Long id;
    private String title;
    private boolean isSecret;
    private String isAnswered;
    private String createdAt;
    private Long productId;
    private String productImageData;
    private String writer;
    private String writerLoginId;

    @QueryProjection
    public InquiryListDto(Long inqueryId, String title, boolean isSecret, boolean isAnswered, LocalDateTime createdAt,
                          String writer, String writerLoginId, Long productId, String productImageUrl) {
        this.id = inqueryId;
        this.productId = productId;
        this.productImageData = productImageUrl;
        this.title = title;
        this.writer = writer.substring(0, 1) + "****";
        this.writerLoginId = writerLoginId;
        this.isSecret = isSecret;
        this.isAnswered = isAnswered ? "답변완료" : "답변대기";
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(createdAt);
    }
}
