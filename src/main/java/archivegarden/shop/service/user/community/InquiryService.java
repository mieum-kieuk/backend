package archivegarden.shop.service.user.community;

import archivegarden.shop.dto.user.community.inquiry.*;
import archivegarden.shop.entity.Inquiry;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.inquiry.InquiryRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;

    /**
     * 상품 문의 저장
     *
     * @throws EntityNotFoundException
     */
    public Long saveInquiry(AddInquiryForm form, Member member) {
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        Inquiry productInquiry = Inquiry.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .isSecret(form.getIsSecret())
                .member(member)
                .product(product)
                .build();

        inquiryRepository.save(productInquiry);

        return productInquiry.getId();
    }

    /**
     * 상품 문의 단건 조회
     */
    public InquiryDetailsDto getInquiry(Long inquiryId) {
        InquiryDetailsDto inquiryDetailsDto = inquiryRepository.findInquiry(inquiryId);
        String encodedImageData = productImageService.getEncodedImageData(inquiryDetailsDto.getProductImageData());
        inquiryDetailsDto.setProductImageData(encodedImageData);
        return inquiryDetailsDto;
    }

    /**
     * 상품 문의 목록 조회
     */
    public Page<InquiryListDto> getInquires(Pageable pageable) {
        Page<InquiryListDto> inquiryListDtos = inquiryRepository.findInquiries(pageable);
        inquiryListDtos.forEach(i -> {
            String encodedImageData = productImageService.getEncodedImageData(i.getProductImageData());
            i.setProductImageData(encodedImageData);
        });

        return inquiryListDtos;
    }

    /**
     * 상품 상세페이지에서 상품 문의 목록 조회
     */
    public Page<InquiryListInProductDto> getInquiresInProduct(Long productId, Pageable pageable, Member loginMember) {
        Page<InquiryListInProductDto> inquiryListDtos = inquiryRepository.findInquiriesByProductId(productId, pageable);
        inquiryListDtos.forEach(i -> {
            if(loginMember != null && i.getWriterLoginId().equals(loginMember.getLoginId())) {
                i.setIsWriter(true);
            }

            String encodedWriterLoginId = i.getWriterLoginId().substring(0, i.getWriterLoginId().length() - 3) + "***";
            i.setWriterLoginId(encodedWriterLoginId);
        });

        return inquiryListDtos;
    }

    /**
     * 상품 문의 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    public EditInquiryForm getInquiryEditForm(Long inquiryId) {
        EditInquiryForm editInquiryForm = inquiryRepository.findInquiryForEdit(inquiryId);
        String encodedImageData = productImageService.getEncodedImageData(editInquiryForm.getProductImageData());
        editInquiryForm.setProductImageData(encodedImageData);
        return editInquiryForm;
    }

    /**
     * 상품 문의 수정
     *
     * @throws EntityNotFoundException
     */
    public void editInquiry(EditInquiryForm form, Long inquiryId) {
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글 입니다."));
        inquiry.update(form, product);
    }

    /**
     * 상품 문의 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteInquiry(Long inquiryId) {
        Inquiry productInquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 게시글 입니다."));
        inquiryRepository.delete(productInquiry);
    }
}
