package archivegarden.shop.repository.shop;

import archivegarden.shop.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @Query("select w from Wish w where w.product.id = :productId and w.member.id = :memberId")
    Wish findWish(@Param("productId") Long productId, @Param("memberId") Long memberId);
}
