package archivegarden.shop.service.admin.help;

import archivegarden.shop.dto.admin.help.notice.*;
import archivegarden.shop.entity.Admin;
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
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 저장
     */
    public Long saveNotice(AddNoticeForm form, Admin admin) {
        //공지사항 생성
        Notice notice = Notice.createNotice(form, admin);

        //공지사항 저장
        noticeRepository.save(notice);

        return notice.getId();
    }

    /**
     * 공지사항 단건 조회
     *
     * @throws NoSuchElementException
     */
    @Transactional(readOnly = true)
    public NoticeDetailsDto getNotice(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공지사항 입니다."));

        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회
     *
     * 검색 & 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(NoticeSearchForm form, Pageable pageable) {
        return noticeRepository.findNoticeAll(form, pageable).map(NoticeListDto::new);
    }

    /**
     * 공지사항 수정 폼 조회
     *
     * @throws NoSuchElementException
     */
    @Transactional(readOnly = true)
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공지사항입니다."));

        return new EditNoticeForm(notice);
    }

    /**
     * 공지사항 수정
     *
     * @throws NoSuchElementException
     */
    public void editNotice(Long noticeId, EditNoticeForm form) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공지사항입니다."));

        //공지사항 수정
        notice.update(form.getTitle(), form.getContent());
    }

    /**
     * 공지사항 단건 삭제
     *
     * @throws NoSuchElementException
     */
    public void deleteNotice(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 공지사항 입니다."));

        //공지사항 삭제
        noticeRepository.delete(notice);
    }
}
