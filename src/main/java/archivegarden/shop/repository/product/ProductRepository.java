package archivegarden.shop.repository.product;

import archivegarden.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("select p from Product p where p.name = :name")
    Optional<Product> findByName(String name);
}
