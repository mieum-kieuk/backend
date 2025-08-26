package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.api.EntityNotFoundApiException;
import archivegarden.shop.exception.global.DuplicateEntityException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.admin.AdminAdminRepository;
import archivegarden.shop.service.admin.email.AdminEmailService;
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

    private final PasswordEncoder passwordEncoder;
    private final AdminEmailService emailService;
    private final AdminAdminRepository adminRepository;

    /**
     * 관리자 회원가입
     *
     * @param form 관리자 회원가입 폼 DTO
     * @return 저장된 관리자의 ID
     */
    public Long join(JoinAdminForm form) {
        encodePassword(form);

        Admin admin = Admin.createAdmin(form);
        adminRepository.save(admin);

        return admin.getId();
    }

    /**
     * 관리자 중복 여부 검사
     *
     * 로그인 아이디, 이메일 중 하나라도 기존 관리자와 중복되는지 확인합니다.
     *
     * @param form 관리자 회원가입 폼 DTO
     * @throws DuplicateEntityException 이미 존재하는 관리자일 경우
     */
    @Transactional(readOnly = true)
    public void checkAdminDuplicate(JoinAdminForm form) {
        adminRepository.findDuplicateAdmin(form.getLoginId(), form.getEmail())
                .ifPresent(admin -> {
                    throw new DuplicateEntityException("이미 존재하는 관리자입니다.");
                });
    }

    /**
     * 관리자 회원가입 완료 페이지에서 필요한 정보 조회
     *
     * @param adminId 관리자 ID
     * @return  회원가입 완료 정보 DTO
     * @throws EntityNotFoundException 관리자가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public JoinSuccessDto getJoinSuccessInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 관리자입니다."));
        return new JoinSuccessDto(admin.getLoginId(), admin.getName(), admin.getEmail());
    }

    /**
     * 로그인 아이디 사용 가능 여부 검사
     *
     * @param loginId 로그인 아이디
     * @return 사용 가능하면 true, 이미 존재하면 false
     */
    @Transactional(readOnly = true)
    public boolean isAvailableLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 이메일 사용 가능 여부 검사
     *
     * @param email 이메일
     * @return 사용 가능하면 true, 이미 존재하면 false
     */
    @Transactional(readOnly = true)
    public boolean isAvailableEmail(String email) {
        return adminRepository.findByEmail(email).isEmpty();
    }

    /**
     * 관리자 목록 조회
     *
     * @param cond     검색 조건을 담은 DTO
     * @param pageable 페이징 정보를 담은 객체
     * @return 검색 조건에 해당하는 관리자 목록 페이지 DTO
     */
    @Transactional(readOnly = true)
    public Page<AdminListDto> getAdmins(AdminSearchCondition cond, Pageable pageable) {
        return adminRepository.findAdmins(cond, pageable);
    }

    /**
     * 관리자 삭제
     *
     * @param adminId 관리자 ID
     * @throws EntityNotFoundApiException 관리자가 존재하지 않는 경우
     */
    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundApiException("존재하지 않는 관리자입니다."));
        adminRepository.delete(admin);
    }

    /**
     * 관리자 권한 부여
     *
     * 관리자 권한을 부여하고 권한 부여 완료 이메일을 해당 관리자에게 전송합니다.
     *
     * @param adminId 관리자 ID
     * @throws EntityNotFoundApiException 관리자가 존재하지 않는 경우
     */
    public void authorizeAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundApiException("존재하지 않는 관리자입니다."));

        admin.authorize();

        emailService.sendAdminAuthComplete(admin.getEmail(), admin.getName());
    }

    /**
     * 비밀번호 암호화
     *
     * @param form 관리자 회원가입 폼 DTO
     */
    private void encodePassword(JoinAdminForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }
}
