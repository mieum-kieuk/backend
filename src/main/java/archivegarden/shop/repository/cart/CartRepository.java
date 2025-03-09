package archivegarden.shop.repository.cart;

import archivegarden.shop.entity.Cart;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    Cart findByMemberAndProduct(Member member, Product product);

    @Modifying
    @Query("delete from Cart c where c.member = :member and c.product = :product")
    void deleteByMemberAndProduct(@Param("member") Member member, @Param("product") Product product);

    @Query("select c from Cart c where c.member = :member and c.product.id in :productIds")
    List<Cart> findOrderProducts(@Param("member") Member member, @Param("productIds") List<Long> productIds);
}
