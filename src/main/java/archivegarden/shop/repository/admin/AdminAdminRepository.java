package archivegarden.shop.repository.admin;

import archivegarden.shop.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminAdminRepository extends JpaRepository<Admin, Long>, AdminAdminRepositoryCustom {

    Optional<Admin> findByLoginId(String loginId);

    Optional<Admin> findByEmail(String email);

    @Query("""
            SELECT a
            FROM Admin a
            WHERE a.loginId = :loginId
                OR a.email = :email
            """)
    Optional<Admin> findDuplicateAdmin(@Param("loginId") String loginId, @Param("email") String email);
}
