package archivegarden.shop.repository;

import archivegarden.shop.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("select s from Delivery s where s.member.id = :memberId order by s.isDefaultAddress desc")
    List<Delivery> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("select s from Delivery s where s.member.id = :memberId and s.isDefaultAddress = 'TRUE'")
    Delivery findDefaultShippingAddress(@Param("memberId") Long memberId);
}
