package archivegarden.shop.security.service;

import archivegarden.shop.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AdminContext extends User {

    private final Admin admin;

    public AdminContext(Admin admin, Collection<? extends GrantedAuthority> authorities) {
        super(admin.getLoginId(), admin.getPassword(), authorities);
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }
}
