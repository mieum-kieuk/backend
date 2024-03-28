package archivegarden.shop.repository.admin.shop;

import archivegarden.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProductRepository extends JpaRepository<Product, Long> {
}
