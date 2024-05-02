package archivegarden.shop.service.community;

import archivegarden.shop.dto.community.qna.AddQnaForm;
import archivegarden.shop.dto.community.qna.EditQnaForm;
import archivegarden.shop.dto.community.qna.QnaDetailsDto;
import archivegarden.shop.dto.community.qna.QnaListDto;
import archivegarden.shop.entity.*;
import archivegarden.shop.exception.NoSuchBoardException;
import archivegarden.shop.exception.NoSuchProductException;
import archivegarden.shop.repository.community.BoardRepository;
import archivegarden.shop.repository.community.QnaRepository;
import archivegarden.shop.repository.shop.ProductRepository;
import archivegarden.shop.service.upload.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final FileStore fileStore;
    private final QnaRepository qnaRepository;
    private final BoardRepository boardRepository;
    private final ProductRepository productRepository;

    /**
     * Qna 저장
     *
     * @throws NoSuchProductException
     */
    public Long saveQnA(AddQnaForm form, Member member) throws IOException {
        //Product 엔티티 조회
        Product product = form.getProductId() != null ? productRepository.findById(form.getProductId())
                .orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다.")) : null;

        //BoardImage 엔티티 생성
        List<BoardImage> boardImages = createBoardImages(form);

        //Qna 엔티티 생성
        Board board = Qna.builder()
                .form(form)
                .member(member)
                .product(product)
                .images(boardImages)
                .build();

        //Qna 저장
        boardRepository.save(board);

        return board.getId();
    }

    /**
     * Qna 단건 조회
     *
     * @throws NoSuchBoardException
     */
    public QnaDetailsDto getQna(Long qnaId) {
        //Qna 조회
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 Q&A입니다."));

        //조회수 증가
        qna.addHit();

        return new QnaDetailsDto(qna);
    }

    /**
     * Qna 목록 조회
     */
    public Page<QnaListDto> getQnas(Pageable pageable) {
        return qnaRepository.findAll(pageable).map(QnaListDto::new);
    }

    /**
     * Qna 수정 폼 조회
     *
     * @throws NoSuchBoardException
     */
    public EditQnaForm getEditQnaForm(Long qnaId) {
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 Q&A 입니다."));

        return new EditQnaForm(qna);
    }

    /**
     * Qna 삭제
     *
     * @throws NoSuchBoardException
     */
    public void deleteQna(Long qnaId) {
        //엔티티 조회
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 Q&A 입니다."));

        //Qna 삭제
        qnaRepository.delete(qna);
    }

    /**
     * MultipartFile -> BoardImage
     */
    private List<BoardImage> createBoardImages(AddQnaForm form) throws IOException {
        List<BoardImage> boardImages = new ArrayList<>();
        if (!form.getImage1().getOriginalFilename().equals("")) {
            boardImages.add(fileStore.storeBoardImage(form.getImage1()));
        }
        if (!form.getImage2().getOriginalFilename().equals("")) {
            boardImages.add(fileStore.storeBoardImage(form.getImage2()));
        }
        if (!form.getImage3().getOriginalFilename().equals("")) {
            boardImages.add(fileStore.storeBoardImage(form.getImage3()));
        }
        if (!form.getImage4().getOriginalFilename().equals("")) {
            boardImages.add(fileStore.storeBoardImage(form.getImage4()));
        }
        if (!form.getImage5().getOriginalFilename().equals("")) {
            boardImages.add(fileStore.storeBoardImage(form.getImage4()));
        }

        return boardImages;
    }
}
