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

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.loginId = :loginId
                OR m.phonenumber = :phonenumber
                OR m.email = :email
            """)
    Optional<Member> findDuplicateMember(@Param("loginId") String loginId, @Param("phonenumber") String phonenumber, @Param("email") String email);


    @Query("""
            SELECT m.id
            FROM Member m
            WHERE m.name = :name
                AND m.email = :email
            """)
    Optional<Long> findMemberIdByNameAndEmail(@Param("name") String name, @Param("email") String email);

    @Query("""
            SELECT m.id
            FROM Member m
            WHERE m.name = :name
              AND m.phonenumber = :phonenumber
            """)
    Optional<Long> findMemberIdByNameAndPhonenumber(@Param("name") String name, @Param("phonenumber") String phonenumber);

    @Query("""
            SELECT m.id 
            FROM Member m 
            WHERE m.loginId = :loginId 
                AND m.name = :name
                AND m.email = :email
            """)
    Optional<Long> findMemberIdByLoginIdAndNameAndEmail(@Param("loginId") String loginId, @Param("name") String name, @Param("email") String email);

    @Query("""
            SELECT m.id 
            FROM Member m 
            WHERE m.loginId = :loginId 
                AND m.name = :name
                AND m.phonenumber = :phonenumber
            """)
    Optional<Long> findMemberIdByLoginIdAndNameAndPhonenumber(@Param("loginId") String loginId, @Param("name") String name, @Param("phonenumber") String phonenumber);
}
