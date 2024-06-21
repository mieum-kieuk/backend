package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.answer.AnswerResponseDto;
import archivegarden.shop.dto.admin.product.answer.EditAnswerRequestDto;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.ProductInquiry;
import archivegarden.shop.exception.Answer;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.answer.AnswerRepository;
import archivegarden.shop.repository.productInquiry.ProductInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductInquiryAnswerService {

    private final AnswerRepository answerRepository;
    private final ProductInquiryRepository inquiryRepository;

    /**
     * 답변 저장
     *
     * @throws AjaxNotFoundException
     */
    public void writeAnswer(String content, Long inquiryId, Admin admin) {
        //ProductInquiry 조회
        ProductInquiry productInquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품문의입니다."));

        //Answer 생성
        Answer answer = Answer.createAnswer(content, productInquiry, admin);

        //Answer 저장
        answerRepository.save(answer);

        //답변대기 -> 답변완료로 수정
        productInquiry.updateAnswerStatus(true);
    }

    /**
     * 답변 조회
     * @param inquiryId
     */
    @Transactional(readOnly = true)
    public AnswerResponseDto getAnswer(Long inquiryId) {
        return answerRepository.findByProductInquiryId(inquiryId)
                .map(answer -> new AnswerResponseDto(answer))
                .orElse(new AnswerResponseDto());
    }

    /**
     * 답변 수정
     *
     * @throws AjaxNotFoundException
     */
    public void editAnswer(EditAnswerRequestDto request) {
        //Answer 조회
        Answer answer = answerRepository.findById(request.getAnswerId()).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품 문의 답변입니다."));

        //Answer 답변 내용 수정
        answer.update(request.getContent());
    }

    /**
     * 답변 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteAnswer(Long answerId) {
        //Answer 조회
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 상품 문의 답변입니다."));

        //Answer 삭제
        answerRepository.delete(answer);

        //답변대기 -> 답변완료로 수정
        ProductInquiry productInquiry = answer.getProductInquiry();
        productInquiry.updateAnswerStatus(false);
    }
}
