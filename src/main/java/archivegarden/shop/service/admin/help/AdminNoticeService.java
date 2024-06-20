package archivegarden.shop.service.admin.help;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import archivegarden.shop.dto.admin.help.notice.EditNoticeForm;
import archivegarden.shop.dto.admin.help.notice.NoticeDetailsDto;
import archivegarden.shop.dto.admin.help.notice.NoticeListDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.admin.AdminNotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
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
        //Notice 생성
        Notice notice = Notice.createNotice(form, admin);

        //Notice 저장
        noticeRepository.save(notice);

        return notice.getId();
    }

    /**
     * 공지사항 단건 조회
     *
     * @throws AdminNotFoundException
     */
    @Transactional(readOnly = true)
    public NoticeDetailsDto getNotice(Long noticeId) {
        //Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 공지사항 입니다."));

        return new NoticeDetailsDto(notice);
    }

    /**
     * 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(AdminSearchForm form, Pageable pageable) {
        return noticeRepository.findAdminNoticeAll(form, pageable).map(NoticeListDto::new);
    }

    /**
     * 공지사항 수정 폼 조회
     *
     * @throws AdminNotFoundException
     */
    @Transactional(readOnly = true)
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        //Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 공지사항입니다."));

        return new EditNoticeForm(notice);
    }

    /**
     * 공지사항 수정
     *
     * @throws AdminNotFoundException
     */
    public void editNotice(Long noticeId, EditNoticeForm form) {
        //Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 공지사항입니다."));

        //Notice 수정
        notice.update(form.getTitle(), form.getContent());
    }

    /**
     * 공지사항 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteNotice(Long noticeId) {
        //Notice 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 공지사항 입니다."));

        //Notice 삭제
        noticeRepository.delete(notice);
    }
}
