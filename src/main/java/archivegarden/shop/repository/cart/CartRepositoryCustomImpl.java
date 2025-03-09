package archivegarden.shop.repository.cart;

import archivegarden.shop.dto.order.CartListDto;
import archivegarden.shop.dto.order.QCartListDto;
import archivegarden.shop.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static archivegarden.shop.entity.QCart.cart;
import static archivegarden.shop.entity.QDiscount.discount;
import static archivegarden.shop.entity.QProduct.product;
import static archivegarden.shop.entity.QProductImage.productImage;

public class CartRepositoryCustomImpl implements CartRepositoryCustom {

    private final JPQLQueryFactory queryFactory;

    public CartRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CartListDto> findAllProducts(Long memberId) {
        return queryFactory
                .select(new QCartListDto(
                        product.id,
                        product.name,
                        product.price,
                        cart.count,
                        productImage.imageUrl,
                        product.discount,
                        product.stockQuantity
                ))
                .from(cart)
                .leftJoin(cart.product, product)
                .leftJoin(product.discount, discount)
                .leftJoin(product.productImages, QProductImage.productImage)
                .where(
                        cart.member.id.eq(memberId),
                        imageTypeDisplay()
                ).fetch();
    }

    /**
     * IMAGE TYPE = DISPLAY
     */
    private BooleanExpression imageTypeDisplay() {
        return productImage != null ? productImage.imageType.eq(ImageType.DISPLAY) : null;
    }
}
