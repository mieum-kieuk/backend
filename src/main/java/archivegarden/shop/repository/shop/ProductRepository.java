package archivegarden.shop.repository.shop;

import archivegarden.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory_Name(String categoryName, Pageable pageable);

    @Query("select p from Product p join fetch p.images where p.id = :productId")
    Optional<Product> findAllWithImages(@Param("productId") Long productId);
}
