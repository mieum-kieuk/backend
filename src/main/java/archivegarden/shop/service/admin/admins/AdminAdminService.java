package archivegarden.shop.service.admin.admins;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.admin.admin.AdminAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAdminService {

    private final AdminAdminRepository adminRepository;

    /**
     * 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminListDto> getAdmins(AdminSearchForm form, Pageable pageable) {
        return adminRepository.findDtoAll(form, pageable);
    }

    /**
     * 관리자 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteAdmin(Long adminId) {
        //Admin 조회
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 관리자입니다."));

        //Admin 삭제
        adminRepository.delete(admin);
    }

    /**
     * 관리자 권한 부여
     *
     * @throws AjaxNotFoundException
     */
    public void authorizeAdmin(Long adminId) {
        //Admin 조회
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 관리자입니다."));

        //권한 부여
        admin.authorize();
    }
}
