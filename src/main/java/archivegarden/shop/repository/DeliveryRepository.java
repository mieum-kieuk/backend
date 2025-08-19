package archivegarden.shop.repository;

import archivegarden.shop.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("""
            SELECT  d
            FROM Delivery  d 
            WHERE d.member.id = :memberId
            ORDER BY d.isDefault DESC
            """)
    List<Delivery> findMyDeliveries(@Param("memberId") Long memberId);

    @Query("""
            SELECT  d
            FROM Delivery  d 
            WHERE d.member.id = :memberId
                AND d.isDefault = true
            """)
    Delivery findDefaultDelivery(@Param("memberId") Long memberId);
}
