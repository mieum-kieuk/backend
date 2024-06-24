package archivegarden.shop.repository.product;

import archivegarden.shop.entity.ImageType;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);

    @Query("select p from ProductImage as p where p.imageType = :imageType and p.product = :product")
    ProductImage findDisplayImage(@Param("imageType") ImageType imageType, @Param("product") Product product);

    @Query("select p from ProductImage as p where p.imageType = :imageType and p.product = :product")
    ProductImage findHoverImage(@Param("imageType") ImageType hover, @Param("product") Product product);
}
