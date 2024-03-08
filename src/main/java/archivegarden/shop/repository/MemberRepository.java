package archivegarden.shop.repository;

import archivegarden.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhonenumber(String phonenumber);

    Optional<Member> findByNameAndEmail(String name, String email);

    Optional<Member> findByNameAndPhonenumber(String name, String phonenumber);

    @Query("select m.email from Member m where m.loginId = :loginId and m.name = :name and m.email = :email")
    String findPasswordByEmail(@Param("loginId") String loginId, @Param("name") String name, @Param("email") String email);

    @Query("select m.email from Member m where m.loginId = :loginId and m.name = :name and m.phonenumber = :phonenumber")
    String findPasswordByPhonenumber(@Param("loginId") String loginId, @Param("name") String name, @Param("phonenumber") String phonenumber);
}
