package archivegarden.shop.service.user.community;

import archivegarden.shop.dto.user.community.inquiry.AddInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.InquiryDetailsDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListDto;
import archivegarden.shop.entity.Inquiry;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.NoSuchBoardException;
import archivegarden.shop.exception.NoSuchProductException;
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
     * 상품문의 수정 폼 조회
     *
     * @throws NoSuchBoardException
     */
    public EditInquiryForm getEditForm(Long inquiryId) {
        //Inquiry 조회
        Inquiry productInquiry = inquiryRepository.findByIdWithProduct(inquiryId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        return new EditInquiryForm(productInquiry);
    }

    /**
     * 상품문의 수정
     *
     * @throws NoSuchProductException
     * @throws NoSuchBoardException
     */
    public void editInquiry(EditInquiryForm form, Long inquiryId) {
        //Product 엔티티 조회
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));

        //Inquiry 조회
        Inquiry productInquiry = inquiryRepository.findByIdWithProduct(inquiryId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        //수정
        productInquiry.update(form.getTitle(), form.getContent(), product);
    }

    /**
     * 상품문의 삭제
     *
     * @throws NoSuchBoardException
     */
    public void deleteInquiry(Long qnaId) {
        //Inquiry 조회
        Inquiry productInquiry = inquiryRepository.findById(qnaId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        //Inquiry 삭제
        inquiryRepository.delete(productInquiry);
    }
}
