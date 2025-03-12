package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.answer.AdminAnswerDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.Answer;
import archivegarden.shop.entity.Inquiry;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.answer.AnswerRepository;
import archivegarden.shop.repository.inquiry.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInquiryService {

    private final AdminProductImageService productImageService;
    private final InquiryRepository inquiryRepository;
    private final AnswerRepository answerRepository;

    /**
     * 상품 문의 단건 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminInquiryDetailsDto getInquiry(Long inquiryId) {
        AdminInquiryDetailsDto inquiryDetailsDto = inquiryRepository.findInquiryInAdmin(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의 글입니다."));
        String encodedImageData = productImageService.getEncodedImageData(inquiryDetailsDto.getProductImageData());
        inquiryDetailsDto.setProductImageData(encodedImageData);
        return inquiryDetailsDto;
    }

    /**
     * 상품 문의 여러건 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminInquiryListDto> getInquiries(AdminProductSearchCondition condition, Pageable pageRequest) {
        Page<AdminInquiryListDto> inquiryListDtos = inquiryRepository.findInquiriesInAdmin(condition, pageRequest);
        inquiryListDtos.forEach(i -> {
            String encodedImageData = productImageService.getEncodedImageData(i.getProductImageData());
            i.setProductImageData(encodedImageData);
        });
        return inquiryListDtos;
    }

    /**
     * 답변 등록
     *
     * @throws AjaxEntityNotFoundException
     */
    public Long addAnswer(Long inquiryId, String answerContent, Admin admin) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 상품 문의 글입니다."));

        Answer answer = Answer.createAnswer(answerContent, admin);
        answerRepository.save(answer);

        inquiry.writeAnswer(answer);

        return answer.getId();
    }

    /**
     * 답변 조회
     */
    @Transactional(readOnly = true)
    public AdminAnswerDto getAnswer(Long inquiryId) {
        return inquiryRepository.findAnswerByInquiryId(inquiryId)
                .map(AdminAnswerDto::new)
                .orElse(null);
    }

    /**
     * 답변 수정
     *
     * @throws EntityNotFoundException
     */
    public void updateAnswer(Long inquiryId, String answerContent) {
        Answer answer = inquiryRepository.findAnswerByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));
        answer.update(answerContent);
    }

    /**
     * 답변 삭제
     *
     * @throws EntityNotFoundException
     */
    public void deleteAnswer(Long inquiryId) {
        Answer answer = inquiryRepository.findAnswerByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 답변입니다."));
        answerRepository.delete(answer);

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("상품 문의 글입니다."));
        inquiry.updateAnswerStatus(false);
    }
}
