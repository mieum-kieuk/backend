package archivegarden.shop.service.admin.admin;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.JoinAdminForm;
import archivegarden.shop.dto.common.JoinSuccessDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.DuplicateEntityException;
import archivegarden.shop.exception.common.EntityNotFoundException;
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

    private final AdminEmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AdminAdminRepository adminRepository;

    /**
     * 새로운 관리자 계정을 회원가입합니다.
     *
     * 전달받은 가입 폼 정보를 기반으로 비밀번호를 암호화하고, Admin 엔티티를 생성하여 저장합니다.
     * @param form 회원가입에 필요한 정보를 담은 폼 DTO
     * @return 저장된 관리자 엔티티의 ID
     */
    public Long join(JoinAdminForm form) {
        encodePassword(form);

        Admin admin = Admin.createAdmin(form);
        adminRepository.save(admin);

        return admin.getId();
    }

    /**
     * 관리자 회원가입 완료 페이지에 필요한 정보를 조회합니다.
     *
     * @param adminId 조회할 관리자의 ID
     * @return 회원가입 완료 페이지에 표시될 관리자 정보를 담은 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    public JoinSuccessDto getJoinSuccessInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 관리자입니다."));
        return new JoinSuccessDto(admin.getLoginId(), admin.getName(), admin.getEmail());
    }

    /**
     * 관리자 로그인 아이디와 이메일의 중복 여부를 검사합니다.
     *
     * 회원가입 시 이미 존재하는 아이디나 이메일인 경우 예외를 발생시킵니다.
     * @param form 중복 검사에 사용될 로그인 아이디와 이메일 정보를 담은 폼 DTO
     * @throws DuplicateEntityException 입력된 로그인 아이디나 이메일이 이미 존재할 경우
     */
    public void checkAdminDuplicate(JoinAdminForm form) {
        adminRepository.findDuplicateAdmin(form.getLoginId(), form.getEmail())
                .ifPresent(admin -> {
                    throw new DuplicateEntityException("이미 존재하는 관리자입니다.");
                });
    }

    /**
     * 특정 로그인 아이디의 사용 가능 여부를 확인합니다.
     *
     * @param loginId 중복 검사할 로그인 아이디
     * @return 해당 로그인 아이디를 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    public boolean isLoginIdAvailable(String loginId) {
        return adminRepository.findByLoginId(loginId).isEmpty();
    }

    /**
     * 특정 이메일 주소의 사용 가능 여부를 확인합니다.
     *
     * @param email 중복 검사할 이메일 주소
     * @return 해당 이메일 주소를 사용할 수 있으면 true, 이미 사용 중이면 false
     */
    public boolean isEmailAvailable(String email) {
        return adminRepository.findByEmail(email).isEmpty();
    }

    /**
     * 검색 조건에 따라 관리자 목록을 페이징하여 조회합니다.
     *
     * @param cond 관리자 검색 조건을 담은 DTO
     * @param pageable 페이징 정보를 담은 객체 (페이지 번호, 페이지 크기, 정렬 등)
     * @return 검색 조건에 맞는 관리자 목록을 담은 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<AdminListDto> getAdmins(AdminSearchCondition cond, Pageable pageable) {
        return adminRepository.findAdmins(cond, pageable);
    }

    /**
     * 특정 관리자 계정을 삭제합니다.
     *
     * @param adminId 삭제할 관리자의 ID
     * @throws AjaxEntityNotFoundException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 관리자입니다."));
        adminRepository.delete(admin);
    }

    /**
     * 특정 관리자에게 관리자 권한을 부여합니다.
     *
     * 관리자 권한을 부여하고 권한 부여 완료 이메일을 해당 관리자에게 전송합니다.
     * @param adminId 권한을 부여할 관리자의 ID
     * @throws AjaxEntityNotFoundException 해당 ID를 가진 관리자가 존재하지 않을 경우
     */
    public void authorizeAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 관리자입니다."));
        admin.authorize();

        emailService.sendAdminAuthComplete(admin.getEmail(), admin.getName());
    }

    /**
     * 회원가입 폼의 비밀번호를 암호화합니다.
     *
     * 전달받은 JoinAdminForm의 비밀번호 필드를 암호화된 문자열로 덮어씁니다.
     * @param form 비밀번호 암호화가 필요한 폼 DTO
     */
    private void encodePassword(JoinAdminForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);
    }
}
