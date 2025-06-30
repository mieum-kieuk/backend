package archivegarden.shop.service.user.community;

import archivegarden.shop.dto.user.community.inquiry.*;
import archivegarden.shop.entity.Inquiry;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.EntityNotFoundAjaxException;
import archivegarden.shop.exception.global.EntityNotFoundException;
import archivegarden.shop.repository.inquiry.InquiryRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.user.product.product.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;
    private final Executor executor;

    /**
     * 상품 문의 등록
     *
     * @param form        상품 문의 등록 폼 DTO
     * @param loginMember 현재 로그인한 회원
     * @return 저장된 상품 문의 ID
     * @throws EntityNotFoundException 해당 ID의 상품이 존재하지 않을 경우
     */
    public Long addInquiry(AddInquiryForm form, Member loginMember) {
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));

        Inquiry productInquiry = Inquiry.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .isSecret(form.getIsSecret())
                .member(loginMember)
                .product(product)
                .build();

        inquiryRepository.save(productInquiry);

        return productInquiry.getId();
    }

    /**
     * 상품 문의 상세 조회
     *
     * @param inquiryId 조회할 상품 문의 ID
     * @return 상품 문의 상세 정보 DTO
     * @throws EntityNotFoundException 해당 ID의 상품 문의가 존재하지 않을 경우
     */
    public InquiryDetailsDto getInquiry(Long inquiryId) {
        InquiryDetailsDto inquiryDetailsDto = inquiryRepository.findInquiry(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의글입니다."));
        String encodedImageData = productImageService.downloadAndEncodeImage(inquiryDetailsDto.getProductImageData());
        inquiryDetailsDto.setProductImageData(encodedImageData);
        return inquiryDetailsDto;
    }

    /**
     * 상품 문의 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 상품 문의 목록 DTO Page 객체
     */
    public Page<InquiryListDto> getInquires(Pageable pageable) {
        Page<InquiryListDto> inquiryListDtos = inquiryRepository.findInquiries(pageable);

        List<CompletableFuture<Void>> futures = inquiryListDtos.getContent().stream()
                .map(inquiry -> CompletableFuture.runAsync(() -> {
                    String encodedImageData = productImageService.downloadAndEncodeImage(inquiry.getProductImageData());
                    inquiry.setProductImageData(encodedImageData);
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return inquiryListDtos;
    }

    /**
     * 상품 상세페이지에서 상품 문의 목록 조회
     *
     * @param productId   조회할 상품 ID
     * @param pageable    페이징 정보
     * @param loginMember 현재 로그인한 회원
     * @return 상세 페이지 상품 목록 DTO Page 객체
     */
    public Page<ProductPageInquiryListDto> getInquiriesInProduct(Long productId, Pageable pageable, Member loginMember) {
        Page<ProductPageInquiryListDto> inquiryListDtos = inquiryRepository.findInquiriesByProductId(productId, pageable);

        inquiryListDtos.forEach(i -> {
            String writerLoginId = i.getWriterLoginId();
            if (writerLoginId != null && writerLoginId.length() > 3) {
                String encodedWriterLoginId = writerLoginId.substring(0, writerLoginId.length() - 3) + "***";
                i.setWriterLoginId(encodedWriterLoginId);
            } else if (writerLoginId != null) {
                i.setWriterLoginId("***");
            }
        });

        return inquiryListDtos;
    }

    /**
     * 상품 문의 수정 폼 조회
     *
     * @param inquiryId   수정할 상품 ID
     * @param loginMember 현재 로그인한 회원
     * @return 상품 문의 수정 폼 DTO
     * @throws EntityNotFoundException 해당 ID의 상품 문의가 존재하지 않을 경우
     * @throws AccessDeniedException   로그인한 회원이 작성자가 아닐 경우 (수정 권한 없음)
     */
    public EditInquiryForm getInquiryEditForm(Long inquiryId, Member loginMember) throws AccessDeniedException {
        EditInquiryForm editInquiryForm = inquiryRepository.findInquiryForEdit(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의글입니다."));

        if (!editInquiryForm.getWriterLoginId().equals(loginMember.getLoginId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        String encodedImageData = productImageService.downloadAndEncodeImage(editInquiryForm.getProductImageData());
        editInquiryForm.setProductImageData(encodedImageData);
        return editInquiryForm;
    }

    /**
     * 상품 문의 수정
     *
     * @param inquiryId 수정할 상품 문의 ID
     * @param form      상품 문의 수정 폼 DTO
     * @throws EntityNotFoundException 상품 또는 이미지가 존재하지 않을 경우
     * @throws AccessDeniedException   로그인한 회원이 작성자가 아닐 경우 (수정 권한 없음)
     */
    public void editInquiry(Long inquiryId, EditInquiryForm form, Member loginMember) throws AccessDeniedException {
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        Inquiry inquiry = inquiryRepository.findByIdWithMember(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의글입니다."));

        if (!inquiry.getMember().getLoginId().equals(loginMember.getLoginId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        inquiry.update(form, product);
    }

    /**
     * 상품 문의 삭제
     *
     * @param inquiryId 삭제할 상품 문의 ID
     * @throws EntityNotFoundAjaxException 해당 ID의 상품 문의가 존재하지 않을 경우
     */
    public void deleteInquiry(Long inquiryId) {
        Inquiry productInquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundAjaxException("존재하지 않는 상품 문의글입니다."));
        inquiryRepository.delete(productInquiry);
    }

    /**
     * 내 상품 문의
     */
    public Page<MyPageInquiryListDto> getMyInquires(Long memberId, Pageable pageable) {
        Page<MyPageInquiryListDto> myInquiries = inquiryRepository.findMyInquiries(memberId, pageable);
        myInquiries.forEach(i -> {
//            String encodedImageData = productImageService.getEncodedImageData(i.getProductImageData());
//            i.setProductImageData(encodedImageData);
        });

        return myInquiries;
    }
}
