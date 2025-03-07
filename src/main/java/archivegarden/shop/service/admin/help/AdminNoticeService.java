package archivegarden.shop.service.admin.help;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import archivegarden.shop.dto.admin.help.notice.EditNoticeForm;
import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.admin.help.notice.NoticeListDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 저장
     */
    public Long saveNotice(AddNoticeForm form, Admin admin) {
        Notice notice = Notice.createNotice(form, admin);
        noticeRepository.save(notice);
        return notice.getId();
    }

    /**
     * 공지사항 단건 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public NoticeDetailsDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항 입니다."));
        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(AdminSearchCondition form, Pageable pageable) {
        return noticeRepository.findAllNoticeInAdmin(form, pageable).map(NoticeListDto::new);
    }

    /**
     * 공지사항 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
        return new EditNoticeForm(notice);
    }

    /**
     * 공지사항 수정
     *
     * @throws EntityNotFoundException
     */
    public void editNotice(Long noticeId, EditNoticeForm form) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
        notice.update(form.getTitle(), form.getContent());
    }

    /**
     * Ajax: 공지사항 단건 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 공지사항 입니다."));
        noticeRepository.delete(notice);
    }
}
