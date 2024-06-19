package archivegarden.shop.service.community;

import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.community.notice.NoticeSearchForm;
import archivegarden.shop.dto.community.notice.NoticeListDto;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.repository.admin.help.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 단건 조회
     *
     * @throws NoSuchElementException
     */
    public NoticeDetailsDto getNotice(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공지사항 입니다."));;

        //조회수 증가
        notice.addHit();

        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회
     *
     * 검색 & 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(NoticeSearchForm form, Pageable pageable) {
        return noticeRepository.findNoticeAll(form, pageable).map(n -> new NoticeListDto(n));
    }
}
