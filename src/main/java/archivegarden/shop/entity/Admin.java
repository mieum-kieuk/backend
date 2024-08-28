package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "login_id", length = 20, nullable = false)
    private String loginId;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 5, nullable = false)
    private String name;

    @Column(length = 45, nullable = false)
    private String email;

    @Column(name = "is_authorized", nullable = false)
    private boolean isAuthorized;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    //==비즈니스 로직==//
    /**
     * 관리자 권한 부여
     */
    public void authorize() {
        this.isAuthorized = true;
        this.authority = Authority.ROLE_ADMIN;
    }

    //==생성자 메서드==//
    public static Admin createAdmin(JoinAdminForm form) {
        Admin admin = new Admin();
        admin.loginId = form.getLoginId();
        admin.password = form.getPassword();
        admin.name = form.getName();
        admin.email = form.getEmail();
        admin.isAuthorized = false;
        admin.authority = Authority.ROLE_ANONYMOUS;
        return admin;
    }
}
