package archivegarden.shop.repository.order;

import archivegarden.shop.dto.order.OrderProductListDto;
import archivegarden.shop.dto.order.QOrderProductListDto;
import archivegarden.shop.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QOrder.order;
import static archivegarden.shop.entity.QOrderProduct.orderProduct;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    @Override
    public List<OrderProductListDto> findOrderProducts(Long orderId) {
        return queryFactory
                .select(new QOrderProductListDto(
                        product.id,
                        product.name,
                        product.price,
                        orderProduct.count,
                        productImage.imageUrl,
                        discount
                ))
                .from(order)
                .leftJoin(order.orderProducts, orderProduct)
                .join(orderProduct.product, product)
                .leftJoin(product.discount, discount)
                .leftJoin(product.productImages, productImage)
                .where(
                        order.id.eq(orderId),
                        imageTypeDisplay()
                )
                .fetch();
    }

    /**
     * IMAGE TYPE = DISPLAY
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }
}
