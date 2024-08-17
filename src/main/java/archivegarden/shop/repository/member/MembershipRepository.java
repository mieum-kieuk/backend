package archivegarden.shop.repository.member;

import archivegarden.shop.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByLevel(String level);
}
