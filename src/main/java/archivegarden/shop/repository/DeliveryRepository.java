package archivegarden.shop.repository;

import archivegarden.shop.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("select d from Delivery d where d.member.id = :memberId order by d.isDefaultDelivery desc")
    List<Delivery> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("select d from Delivery d where d.member.id = :memberId and d.isDefaultDelivery = 'TRUE'")
    Delivery findDefaultDelivery(@Param("memberId") Long memberId);
}
