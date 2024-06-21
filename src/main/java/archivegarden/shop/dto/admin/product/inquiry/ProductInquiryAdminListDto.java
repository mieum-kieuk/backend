package archivegarden.shop.dto.admin.product.inquiry;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ProductInquiryAdminListDto {

    private Long id;
    private String title;
    private String writerName;
    private String createdAt;
    private boolean isAnswered;

    @QueryProjection
    public ProductInquiryAdminListDto(Long id, String title, String name, LocalDateTime createdAt, String isAnswered) {
        this.id = id;
        this.title = title;
        this.writerName = name;
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(createdAt);
        this.isAnswered = Boolean.parseBoolean(isAnswered);
    }
}

