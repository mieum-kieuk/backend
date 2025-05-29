package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "PRODUCT_IMAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //양방향

    public static ProductImage createProductImage(String originalFilename, String imageDownloadUrl, ImageType imageType) {
        ProductImage productImage = new ProductImage();
        productImage.imageName = originalFilename;
        productImage.imageUrl = imageDownloadUrl;
        productImage.imageType = imageType;
        return productImage;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
