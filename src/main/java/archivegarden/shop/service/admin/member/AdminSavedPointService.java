package archivegarden.shop.service.admin.member;

import archivegarden.shop.dto.admin.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.member.SavedPointListDto;
import archivegarden.shop.repository.point.SavedPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminSavedPointService {

    private final SavedPointRepository savedPointRepository;

    /**
     * 적립된 적립금 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<SavedPointListDto> getSavedPoints(AdminSearchForm form, Pageable pageable) {
        return savedPointRepository.findDtoAll(form, pageable);
    }
}
