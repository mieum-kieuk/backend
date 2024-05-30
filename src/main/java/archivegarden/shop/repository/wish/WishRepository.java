package archivegarden.shop.repository.wish;

import archivegarden.shop.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long>, WishRepositoryCustom {

    @Query("select w from Wish w where w.product.id = :productId and w.member.id = :memberId")
    Optional<Wish> findWish(@Param("productId") Long productId, @Param("memberId") Long memberId);
}
