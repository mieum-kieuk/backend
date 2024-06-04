package archivegarden.shop.service.admin.member;

import archivegarden.shop.dto.admin.member.AddAdminForm;
import archivegarden.shop.dto.member.NewMemberInfo;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.repository.admin.member.AdminAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAdminService {

    private final PasswordEncoder passwordEncoder;
    private final AdminAdminRepository adminRepository;

    /**
     * 관리자 회원가입
     */
    public Integer join(AddAdminForm form) {

        //중복 관리자 검증
        validateDuplicateAdmin(form);

        //비밀번호 암호화
        encodePassword(form);
        passwordEncoder.encode(form.getPassword());

        //관리자 생성
        Admin admin = Admin.createAdmin(form);

        //관리자 저장
        adminRepository.save(admin);

        return admin.getId();
    }

    private void encodePassword(AddAdminForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }

    /**
     * 회원 가입 완료페이지에서 필요한 정보 조회
     *
     * @throws NoSuchElementException
     */
    public NewMemberInfo getNewAdminInfo(Integer adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 관리자입니다."));
        return new NewMemberInfo(admin.getLoginId(), admin.getName(), admin.getEmail());
    }

    /**
     * 중복 관리자 검증
     *
     * @throws IllegalStateException
     */
    private void validateDuplicateAdmin(AddAdminForm form) {
        adminRepository.findDuplicateAdmin(form.getLoginId(), form.getEmail())
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 존재하는 관리자입니다.");
                });
    }

    /**
     * 관리자 아이디 중복 검사
     */
    public boolean isAvailableLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 관리자 이메일 중복 검사
     */
    public boolean isAvailableEmail(String email) {
        return adminRepository.findByEmail(email).isEmpty();
    }
}
