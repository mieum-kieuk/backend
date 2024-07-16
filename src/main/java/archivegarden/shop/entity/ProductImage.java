package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "PRODUCT_IMAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @Column(name = "upload_image_name", nullable = false)
    private String uploadImageName;

    @Column(name = "store_image_name", nullable = false)
    private String storeImageName;

    @Column(name = "image_type", length = 10, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //다대일 양방향

    //==연관관계 메서드==//
    public void setProduct(Product product) {
        this.product = product;
    }

    //==생성자 메서드==//
    public static ProductImage createProductImage(String uploadImageName, String storeImageName, ImageType imageType) {
        ProductImage productImage = new ProductImage();
        productImage.uploadImageName = uploadImageName;
        productImage.storeImageName = storeImageName;
        productImage.imageType = imageType;
        return productImage;
    }
}
