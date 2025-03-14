package archivegarden.shop.repository.order;

import archivegarden.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o.orderStatus from Order o where o.merchantUid = :paymentId")
    String findOrderStatusByMerchantUid(@Param("paymentId") String paymentId);
}
