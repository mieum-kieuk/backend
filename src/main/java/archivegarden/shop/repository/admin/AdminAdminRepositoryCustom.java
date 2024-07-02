package archivegarden.shop.repository.admin;

import archivegarden.shop.dto.admin.admin.AdminListDto;
import archivegarden.shop.dto.admin.AdminSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminAdminRepositoryCustom {

    Page<AdminListDto> findDtoAll(AdminSearchForm form, Pageable pageable);
}
