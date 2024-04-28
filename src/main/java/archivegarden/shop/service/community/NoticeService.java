package archivegarden.shop.service.community;

import archivegarden.shop.dto.community.notice.*;
import archivegarden.shop.entity.Board;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Notice;
import archivegarden.shop.exception.NoSuchBoardException;
import archivegarden.shop.repository.community.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final BoardRepository boardRepository;

    /**
     * 공지사항 저장
     */
    public Long saveNotice(AddNoticeForm form, Member member) {
        //엔티티 생성
        Board board = Notice.builder()
                .form(form)
                .member(member)
                .build();

        //공지사항 저장
        boardRepository.save(board);

        return board.getId();
    }

    /**
     * 공지사항 단건 조회
     *
     * @return NoticeDetailsDto
     * @throws NoSuchBoardException
     */
    public NoticeDetailsDto getNotice(Long noticeId) {
        //공지사항 조회
        Board board = boardRepository.findByIdWithMember(noticeId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 공지사항 입니다."));

        //조회수 증가
        board.addHit();

        return new NoticeDetailsDto(board);
    }

    /**
     * 공지사항 목록 조회
     * 검색
     * 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<NoticeListDto> getNotices(NoticeSearchForm form, Pageable pageable) {
        return boardRepository.findNoticeAll(form, pageable).map(n -> new NoticeListDto(n));
    }

    /**
     * 공지사항 단건 삭제
     *
     * @throws NoSuchBoardException
     */
    public void deleteNotice(Long noticeId) {
        //엔티티 조회
        Board board = boardRepository.findById(noticeId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 공지사항 입니다."));

        //공지사항 삭제
        boardRepository.delete(board);
    }

    /**
     * 공지사항 수정 폼 조회
     *
     * @return EditNoticeForm
     * @throws NoSuchBoardException
     */
    @Transactional(readOnly = true)
    public EditNoticeForm getEditNoticeForm(Long noticeId) {
        //공지사항 조회
        Board board = boardRepository.findById(noticeId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 공지사항입니다."));

        return new EditNoticeForm(board);
    }

    /**
     * 공지사항 수정
     *
     * @throws NoSuchBoardException
     */
    public void editNotice(Long noticeId, EditNoticeForm form) {
        //엔티티 조회
        Board board = boardRepository.findById(noticeId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 공지사항입니다."));

        //공지사항 수정
        board.update(form.getTitle(), form.getContent());
    }
}
