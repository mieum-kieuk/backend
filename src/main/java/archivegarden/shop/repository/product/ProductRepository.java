package archivegarden.shop.repository.product;

import archivegarden.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("select p from Product p join fetch p.productImages where p.id = :productId")
    Optional<Product> findByIdWithProductImages(@Param("productId") Long productId);
}
