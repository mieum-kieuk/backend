package archivegarden.shop.service.community;

import archivegarden.shop.dto.community.notice.AddNoticeForm;
import archivegarden.shop.dto.community.notice.EditNoticeForm;
import archivegarden.shop.dto.community.notice.NoticeDetailsDto;
import archivegarden.shop.dto.community.notice.NoticeListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.NoSuchNoticeException;
import archivegarden.shop.repository.community.NoticeRepository;
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
     * 공지사항 저장
     */
    public Long saveNotice(AddNoticeForm form, Member member) {
        //엔티티 생성
        Notice notice = Notice.createNotice(form, member);

        //공지사항 저장
        noticeRepository.save(notice);

        return notice.getId();
    }

    /**
     * 공지사항 단건 조회
     *
     * @return NoticeDetailsDto
     * @throws NoSuchNoticeException
     */
    public NoticeDetailsDto getNotice(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchNoticeException("존재하지 않는 공지사항 입니다."));

        //조회수 증가
        notice.addHit();

        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회 + 페이지네이션
     */
    public Page<NoticeListDto> getNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable).map(n -> new NoticeListDto(n));
    }

    /**
     * 공지사항 단건 삭제
     *
     * @throws NoSuchNoticeException
     */
    public void deleteNotice(Long noticeId) {
        //엔티티 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchNoticeException("존재하지 않는 공지사항 입니다."));

        //공지사항 삭제
        noticeRepository.delete(notice);
    }

    /**
     * 공지사항 수정 폼 조회
     *
     * @return EditNoticeForm
     * @throws NoSuchNoticeException
     */
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        //공지사항 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchNoticeException("존재하지 않는 공지사항입니다."));

        return new EditNoticeForm(notice);
    }

    /**
     * 공지사항 수정
     *
     * @throws NoSuchNoticeException
     */
     public void editNotice(Long noticeId, EditNoticeForm form) {
         //엔티티 조회
         Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchNoticeException("존재하지 않는 공지사항입니다."));

         //공지사항 수정
         notice.update(form.getTitle(), form.getContent());
     }
}
