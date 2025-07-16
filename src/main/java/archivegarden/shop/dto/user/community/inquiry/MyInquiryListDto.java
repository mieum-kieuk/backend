package archivegarden.shop.dto.user.community.inquiry;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class MyInquiryListDto {

    private Long id;
    private String title;
    private String content;
    private boolean isSecret;
    private String createdAt;
    private Long productId;
    private String productDisplayImage;
    private String answer;
    private boolean isAnswered;
    private String answeredAt;

    @QueryProjection
    public MyInquiryListDto(Long id, String title, String content, boolean isSecret, LocalDateTime createdAt, Long productId, String productImageUrl, String answer, LocalDateTime answeredAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(createdAt);;
        this.productId = productId;
        this.productDisplayImage = productImageUrl;
        this.answer = answer;
        this.isAnswered = answer != null;
        if(answer != null) {
            this.answeredAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").format(answeredAt);
        }
    }
}
