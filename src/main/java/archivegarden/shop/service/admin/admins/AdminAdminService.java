package archivegarden.shop.service.admin.admins;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.admin.AdminSearchForm;
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
    public Page<AdminListDto> getAdmins(AdminSearchForm form, Pageable pageable) {
        return adminRepository.findDtoAll(form, pageable);
    }
}
