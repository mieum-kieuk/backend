package archivegarden.shop.repository.point;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.member.SavedPointListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedPointRepositoryCustom {

    Page<SavedPointListDto> findDtoAll(AdminSearchForm form, Pageable pageable);
}
