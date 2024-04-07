package archivegarden.shop.repository.shop;

import archivegarden.shop.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Product> findLatestProducts() {

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.discount,discount).fetchJoin()
                .orderBy(product.createdAt.desc())
                .offset(1)
                .limit(9)
                .fetch();
    }
}
