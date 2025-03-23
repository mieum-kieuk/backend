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
public class ProductPageInquiryListDto {

    private Long id;
    private String title;
    private String content;
    private Boolean isSecret;
    private String isAnswered;
    private String createdAt;
    private String writerLoginId;
    private Boolean isWriter;
    private String answer;

    @QueryProjection
    public ProductPageInquiryListDto(Long inqueryId, String title, String content, boolean isSecret, boolean isAnswered, LocalDateTime createdAt, String writerLoginId, String answer) {
        this.id = inqueryId;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.isAnswered = isAnswered ? "답변완료" : "답변대기";
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(createdAt);
        this.writerLoginId = writerLoginId;
        this.isWriter = false;
        this.answer = answer;
    }
}
