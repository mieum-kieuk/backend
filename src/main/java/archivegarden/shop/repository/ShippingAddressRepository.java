package archivegarden.shop.repository;

import archivegarden.shop.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    @Query("select s from ShippingAddress s where s.member.id = :memberId order by s.isDefaultAddress desc")
    List<ShippingAddress> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("select s from ShippingAddress s where s.member.id = :memberId and s.isDefaultAddress = 'TRUE'")
    ShippingAddress findDefaultShippingAddress(@Param("memberId") Long memberId);
}
