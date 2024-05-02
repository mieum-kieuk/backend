package archivegarden.shop.dto.community.qna;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.Qna;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class QnaDetailsDto {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String writerLoginId;
    private String createdAt;

    private boolean hasProduct;
    private Long productId;
    private String productName;
    private String productPrice;
    private String productImage;

    private List<String> images = new ArrayList<>();

    public QnaDetailsDto(Qna qna) {
        this.id = qna.getId();
        this.title = qna.getTitle();
        this.content = qna.getContent();
        this.writer = qna.getMember().getName().substring(0, 1) + "****";
        this.writerLoginId = qna.getMember().getLoginId();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(qna.getCreatedAt());

        if(qna.getProduct() != null) {
            Product product = qna.getProduct();
            this.hasProduct = true;
            this.productId = product.getId();
            this.productName = product.getName();
            this.productPrice = new DecimalFormat("###,###").format(product.getPrice());
            this.productImage = product.getImages().stream().filter(img -> img.getImageType() == ImageType.DISPLAY).findAny().get().getStoreImageName();
        }

        this.images = qna.getImages().stream().map(img -> img.getStoreImageName()).collect(Collectors.toList());
    }
}
