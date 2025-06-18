package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
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
     * 관리자 회원가입 완료 페이지에 필요한 정보 조회
     *
     * @param adminId 조회할 관리자의 ID
     * @return 회원가입 완료 페이지에 보여줄 관리자 정보를 담은 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public JoinSuccessDto getJoinSuccessInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 관리자입니다."));
        return new JoinSuccessDto(admin.getLoginId(), admin.getName(), admin.getEmail());
    }

    /**
     * 관리자 중복 여부 검사
     *
     * 회원가입 시 해당 아이디나 이메일로 가입된 관리자가 존재하는 경우 예외를 발생시킵니다.
     *
     * @param form 관리자 회원가입 폼 DTO
     * @throws DuplicateEntityException 아이디 또는 이메일로 가입된 관리자 존재할 경우
     */
    @Transactional(readOnly = true)
    public void checkAdminDuplicate(JoinAdminForm form) {
        adminRepository.findDuplicateAdmin(form.getLoginId(), form.getEmail())
                .ifPresent(admin -> {
                    throw new DuplicateEntityException("이미 존재하는 관리자입니다.");
                });
    }

    /**
     * 로그인 아이디 사용 가능 여부 확인
     *
     * @param loginId 중복 검사할 로그인 아이디
     * @return 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return adminRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 이메일 주소 사용 가능 여부 확인
     *
     * @param email 중복 검사할 이메일 주소
     * @return 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
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
     * @param adminId 삭제할 관리자의 ID
     * @throws EntityNotFoundAjaxException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 관리자입니다."));
        adminRepository.delete(admin);
    }

    /**
     * 관리자 권한 부여
     *
     * 관리자 권한을 부여하고 권한 부여 완료 이메일을 해당 관리자에게 전송합니다.
     *
     * @param adminId 권한을 부여할 관리자의 ID
     * @throws EntityNotFoundAjaxException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    public void authorizeAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 관리자입니다."));
        admin.authorize();

        emailService.sendAdminAuthComplete(admin.getEmail(), admin.getName());
    }

    /**
     * 비밀번호 암호화
     *
     * 회원가입 시 비밀번호 필드를 암호화된 문자열로 변경합니다.
     *
     * @param form 관리자 회원가입 폼 DTO
     */
    private void encodePassword(JoinAdminForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }
}
