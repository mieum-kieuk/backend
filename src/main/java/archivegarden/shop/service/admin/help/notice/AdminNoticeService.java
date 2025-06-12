package archivegarden.shop.service.admin.help.notice;

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
     * 새로운 공지사항을 저장하고 생성된 공지사항 ID를 반환합니다.
     *
     * @param form  등록할 공지사항 정보 폼 DTO
     * @param admin 등록 요청을 수행하는 관리자 엔티티
     * @return 저장된 공지사항 엔티티의 ID
     */
    public Long saveNotice(AddNoticeForm form, Admin admin) {
        Notice notice = Notice.createNotice(form, admin);
        noticeRepository.save(notice);
        return notice.getId();
    }

    /**
     * 단일 공지사항을 조회하여 상세 DTO로 반환합니다.
     *
     * @param noticeId 조회할 공지사항의 ID
     * @return 공지사항 상세 정보 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 공지사항이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public NoticeDetailsDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
        return new NoticeDetailsDto(notice);
    }

    /**
     * 검색 조건에 따라 공지사항 목록을 페이징하여 조회합니다.
     *
     * @param cond     공지사항 검색 조건을 담은 DTO
     * @param pageable 페이징 정보를 담은 객체
     * @return 검색 조건에 맞는 공지사항 목록을 담은 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(AdminSearchCondition cond, Pageable pageable) {
        return noticeRepository.findNoticesInAdmin(cond, pageable).map(NoticeListDto::new);
    }

    /**
     * 수정 화면에 필요한 폼 데이터를 조회합니다.
     *
     * @param noticeId 수정할 공지사항의 ID
     * @return 공지사항 수정 폼 DTO
     * @throws EntityNotFoundException 해당 ID를 가진 공지사항이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
        return new EditNoticeForm(notice);
    }

    /**
     * 기존 공지사항의 제목과 내용을 수정합니다.
     *
     * @param noticeId 수정할 공지사항 ID
     * @param form     수정할 제목 및 내용 정보 폼
     * @throws EntityNotFoundException 해당 ID를 가진 공지사항이 존재하지 않을 경우
     */
    public void editNotice(Long noticeId, EditNoticeForm form) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공지사항입니다."));
        notice.update(form.getTitle(), form.getContent());
    }

    /**
     * AJAX 요청으로 공지사항을 삭제합니다.
     *
     * @param noticeId 삭제할 공지사항 ID
     * @throws AjaxEntityNotFoundException 해당 ID를 가진 공지사항이 존재하지 않을 경우
     */
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 공지사항입니다."));
        noticeRepository.delete(notice);
    }
}
