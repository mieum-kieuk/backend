package archivegarden.shop.dto.user.community.inquiry;

import archivegarden.shop.entity.Inquiry;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class InquiryDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String writer;
    private String writerLoginId;
    private Long productId;
    private String productName;
    private String productPrice;
    private String productImageData;
    private String answer;
    private String answeredAt;

    @QueryProjection
    public InquiryDetailsDto(Inquiry inquiry, String writerLoginId, Long productId, String productName,
                             int productPrice, String productImageUrl, String answer, LocalDateTime answeredAt) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").format(inquiry.getCreatedAt());
        this.writer = writerLoginId.substring(0, writerLoginId.length() - 3) + "***";
        this.writerLoginId = writerLoginId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = new DecimalFormat("###,###원").format(productPrice);
        this.productImageData = productImageUrl;
        this.answer = answer;
        if(answer != null) {
            this.answeredAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").format(answeredAt);
        }
    }
}
