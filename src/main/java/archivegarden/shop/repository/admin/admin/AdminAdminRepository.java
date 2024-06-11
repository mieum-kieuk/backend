package archivegarden.shop.repository.admin.admin;

import archivegarden.shop.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminAdminRepository extends JpaRepository<Admin, Integer>, AdminAdminRepositoryCustom {

    Optional<Admin> findByLoginId(String loginId);

    Optional<Admin> findByEmail(String email);

    @Query("select a from Admin a where a.loginId = :loginId or a.email = :email")
    Optional<Admin> findDuplicateAdmin(@Param("loginId") String loginId, @Param("email") String email);
}
