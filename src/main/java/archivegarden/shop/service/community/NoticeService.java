package archivegarden.shop.service.community;

import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.community.notice.NoticeListDto;
import archivegarden.shop.dto.community.notice.NoticeSearchForm;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.NotFoundException;
import archivegarden.shop.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 단건 조회
     *
     * @throws NotFoundException
     */
    public NoticeDetailsDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 입니다."));;
        notice.addHit();
        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회
     * 검색 & 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(NoticeSearchForm form, Pageable pageable) {
        return noticeRepository.findNoticeAll(form, pageable).map(n -> new NoticeListDto(n));
    }
}
