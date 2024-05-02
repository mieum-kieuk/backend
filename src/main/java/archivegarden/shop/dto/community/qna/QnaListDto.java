package archivegarden.shop.dto.community.qna;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Qna;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class QnaListDto {

    private Long id;
    private Long productId;
    private String productImage;
    private String title;
    private String writer;
    private int hit;
    private String createdAt;
    private boolean hasAttachFiles;

    public QnaListDto(Qna qna) {
        this.id = qna.getId();
        if(qna.getProduct() != null) {
            this.productId = qna.getProduct().getId();
            this.productImage = qna.getProduct().getImages().stream().filter(img -> img.getImageType() == ImageType.DISPLAY).findAny().get().getStoreImageName();
        }
        this.title = qna.getTitle();
        this.writer = qna.getMember().getName().substring(0, 1) + "****";
        this.hit = qna.getHit();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(qna.getCreatedAt());
        this.hasAttachFiles = qna.getImages().size() > 0 ? true : false;
    }
}
