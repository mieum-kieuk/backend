package archivegarden.shop.repository.member;

import archivegarden.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, UserMemberRepositoryCustom {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhonenumber(String phonenumber);

    @Query("select m.id from Member m where m.name = :name and m.email = :email")
    Long findLoginIdByEmail(@Param("name") String name, @Param("email") String email);

    @Query("select m.id from Member m where m.name = :name and m.phonenumber = :phonenumber")
    Long findLoginIdByPhonenumber(@Param("name") String name, @Param("phonenumber") String phonenumber);

    @Query("select m.email from Member m where m.loginId = :loginId and m.name = :name and m.email = :email")
    String findPasswordByEmail(@Param("loginId") String loginId, @Param("name") String name, @Param("email") String email);

    @Query("select m.email from Member m where m.loginId = :loginId and m.name = :name and m.phonenumber = :phonenumber")
    String findPasswordByPhonenumber(@Param("loginId") String loginId, @Param("name") String name, @Param("phonenumber") String phonenumber);

    @Query("select m from Member m where m.loginId = :loginId or m.phonenumber = :phonenumber or m.email = :email")
    Optional<Member> findDuplicateMember(@Param("loginId") String loginId, @Param("phonenumber") String phonenumber, @Param("email") String email);
}
