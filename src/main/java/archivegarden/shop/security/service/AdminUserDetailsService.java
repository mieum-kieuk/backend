package archivegarden.shop.security.service;

import archivegarden.shop.entity.Admin;
import archivegarden.shop.repository.admin.member.AdminAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminAdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디로 가입된 관리자가 존재하지 않습니다."));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(admin.getAuthority().toString()));

        return new AdminContext(admin, roles);
    }
}
