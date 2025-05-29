package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String password;

    @Column(length = 5, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(name = "is_authorized", nullable = false)
    private boolean isAuthorized;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    /**
     * 관리자 권한 부여
     */
    public void authorize() {
        this.isAuthorized = true;
        this.authority = Authority.ROLE_ADMIN;
    }
}
