package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.answer.AdminAnswerDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Answer;
import archivegarden.shop.entity.Inquiry;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.answer.AnswerRepository;
import archivegarden.shop.repository.inquiry.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInquiryService {

    private final AdminProductImageService productImageService;
    private final InquiryRepository inquiryRepository;
    private final AnswerRepository answerRepository;
    private final Executor executor;

    /**
     * 상품 문의 상세 조회
     *
     * @param inquiryId 조회할 상품 문의 ID
     * @return 상품 문의 상세 정보 DTO
     * @throws EntityNotFoundException 상품 문의가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public AdminInquiryDetailsDto getInquiry(Long inquiryId) {
        AdminInquiryDetailsDto inquiryDetailsDto = inquiryRepository.findInquiryInAdmin(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의글입니다."));
        String encodedImageData = productImageService.downloadAndEncodeImage(inquiryDetailsDto.getProductDisplayImage());
        inquiryDetailsDto.setProductDisplayImage(encodedImageData);
        return inquiryDetailsDto;
    }

    /**
     * 상품 문의 목록 조회
     *
     * @param cond 상품 문의 검색 조건
     * @param pageable 페이징 정보
     * @return 상품 문의 목록 DTO Page 객체
     */
    @Transactional(readOnly = true)
    public Page<AdminInquiryListDto> getInquiries(AdminProductSearchCondition cond, Pageable pageable) {
        Page<AdminInquiryListDto> inquiryListDtos = inquiryRepository.findInquiriesInAdmin(cond, pageable);

        List<CompletableFuture<Void>> futures = inquiryListDtos.getContent().stream()
                .map(inquiry -> CompletableFuture.runAsync(() -> {
                    String encodedImageData = productImageService.downloadAndEncodeImage(inquiry.getProductDisplayImage());
                    inquiry.setProductDisplayImage(encodedImageData);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return inquiryListDtos;
    }

    /**
     * 상품 문의 답변 등록
     *
     * @param inquiryId     답변을 등록할 상품 문의 ID
     * @param answerContent 답변 내용
     * @param admin         답변 작성 관리자
     * @return 등록된 답변 ID
     * @throws EntityNotFoundAjaxException 상품 문의글이 존재하지 않는 경우
     */
    public Long addAnswer(Long inquiryId, String answerContent, Admin admin) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품 문의글입니다."));

        Answer answer = Answer.createAnswer(answerContent, admin);
        answerRepository.save(answer);

        inquiry.writeAnswer(answer);

        return answer.getId();
    }

    /**
     * 상품 문의 답변 조회
     *
     * @param inquiryId 답변을 조회할 상품 문의 ID
     * @return 답변 DTO, 없으면 null 반환
     */
    @Transactional(readOnly = true)
    public AdminAnswerDto getAnswer(Long inquiryId) {
        return inquiryRepository.findAnswerByInquiryId(inquiryId)
                .map(AdminAnswerDto::new)
                .orElse(null);
    }

    /**
     * 상품 문의 답변 수정
     *
     * @param inquiryId     수정할 상품 문의 ID
     * @param answerContent 수정할 답변 내용
     * @throws EntityNotFoundException 답변이 존재하지 않는 경우
     */
    public void updateAnswer(Long inquiryId, String answerContent) {
        Answer answer = inquiryRepository.findAnswerByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));
        answer.update(answerContent);
    }

    /**
     * 상품 문의 답변 삭제
     *
     * @param inquiryId 삭제할 답변이 속한 상품 문의 ID
     * @throws EntityNotFoundException 답변 또는 상품 문의가 존재하지 않는 경우
     */
    public void deleteAnswer(Long inquiryId) {
        Answer answer = inquiryRepository.findAnswerByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));
        answerRepository.delete(answer);

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의글입니다."));
        inquiry.updateAnswerStatus(false);
    }
}
