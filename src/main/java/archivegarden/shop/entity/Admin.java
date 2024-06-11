package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.admin.AddAdminForm;
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
    private Integer id;

    @Column(name = "login_id", length = 20, nullable = false)
    private String loginId;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 5, nullable = false)
    private String name;

    @Column(length = 45, nullable = false)
    private String email;

    @Column(name = "is_authorized", nullable = false)
    private String isAuthorized;

    @Column(length = 30, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    //==생성자 메서드==//
    public static Admin createAdmin(AddAdminForm form) {
        Admin admin = new Admin();
        admin.loginId = form.getLoginId();
        admin.password = form.getPassword();
        admin.name = form.getName();
        admin.email = form.getEmail();
        admin.isAuthorized = Boolean.FALSE.toString().toUpperCase();
        admin.authority = Authority.ROLE_ANONYMOUS;
        return admin;
    }
}
