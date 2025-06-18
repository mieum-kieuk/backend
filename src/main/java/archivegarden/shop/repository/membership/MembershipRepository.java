package archivegarden.shop.repository.membership;

import archivegarden.shop.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByName(@Param("name") String name);

    @Query("select max(m.level) from Membership m")
    Integer findMaxLevelMembership();

    @Query("select m from Membership m order by m.level asc")
    List<Membership> findAllOrderByLevelAsc();

    @Query("select m from Membership m where m.isDefault = true")
    Membership findDefault();
}
