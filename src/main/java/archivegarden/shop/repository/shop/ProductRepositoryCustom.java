package archivegarden.shop.repository.shop;

import archivegarden.shop.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findLatestProducts();
}
