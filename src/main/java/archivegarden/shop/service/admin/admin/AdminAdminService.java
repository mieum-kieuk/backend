package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinCompletionInfoDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.admin.AdminAdminRepository;
import archivegarden.shop.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAdminService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AdminAdminRepository adminRepository;

    /**
     * 관리자 회원가입
     */
    public Long join(JoinAdminForm form) {
        validateDuplicateAdmin(form);

        encodePassword(form);

        Admin admin = Admin.createAdmin(form);
        adminRepository.save(admin);

        return admin.getId();
    }

    /**
     * 회원 가입 완료페이지에서 필요한 정보 조회
     *
     * @throws NotFoundException
     */
    public JoinCompletionInfoDto getJoinCompletionInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new NotFoundException("존재하지 않는 관리자입니다."));
        return new JoinCompletionInfoDto(admin.getLoginId(), admin.getName(), admin.getEmail());
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

    /**
     * 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminListDto> getAdmins(AdminSearchCondition form, Pageable pageable) {
        return adminRepository.findDtoAll(form, pageable);
    }

    /**
     * 관리자 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 관리자입니다."));
        adminRepository.delete(admin);
    }

    /**
     * 관리자 권한 부여
     *
     * @throws AjaxNotFoundException
     */
    public void authorizeAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 관리자입니다."));
        admin.authorize();

        emailService.sendAuthComplete(admin.getEmail(), admin.getName());
    }

    /**
     * 비밀번호 암호화
     */
    private void encodePassword(JoinAdminForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }

    /**
     * 중복 관리자 검증
     *
     * @throws IllegalStateException
     */
    private void validateDuplicateAdmin(JoinAdminForm form) {
        adminRepository.findDuplicateAdmin(form.getLoginId(), form.getEmail())
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 존재하는 관리자입니다.");
                });
    }
}
