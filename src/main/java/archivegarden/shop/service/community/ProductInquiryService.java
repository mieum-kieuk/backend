package archivegarden.shop.service.community;

import archivegarden.shop.dto.community.inquiry.AddProductInquiryForm;
import archivegarden.shop.dto.community.inquiry.EditProductInquiryForm;
import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
import archivegarden.shop.entity.Member;
import archivegarden.shop.entity.Product;
import archivegarden.shop.entity.ProductInquiry;
import archivegarden.shop.exception.NoSuchBoardException;
import archivegarden.shop.exception.NoSuchProductException;
import archivegarden.shop.repository.productInquiry.ProductInquiryRepository;
import archivegarden.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductInquiryService {

    private final ProductRepository productRepository;
    private final ProductInquiryRepository productInquiryRepository;

    /**
     * 상품문의 저장
     *
     * @throws NoSuchProductException
     */
    public Long saveInquiry(AddProductInquiryForm form, Member member) {
        //Product 엔티티 조회
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));

        //ProductInquiry 엔티티 생성
        ProductInquiry productInquiry = ProductInquiry.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .isSecret(form.getIsSecret())
                .member(member)
                .product(product)
                .build();

        //ProductInquiry 저장
        productInquiryRepository.save(productInquiry);

        return productInquiry.getId();
    }

    /**
     * 상품문의 단건 조회
     */
    public ProductInquiryDetailsDto getInquiry(Long inquiryId) {
        return productInquiryRepository.findDto(inquiryId);
    }

    /**
     * 상품문의 목록 조회
     */
    public Page<ProductInquiryListDto> getInquires(Pageable pageable) {
        return productInquiryRepository.findDtoAll(pageable);
    }

    /**
     * 상품문의 수정 폼 조회
     *
     * @throws NoSuchBoardException
     */
    public EditProductInquiryForm getEditForm(Long inquiryId) {
        //ProductInquiry 조회
        ProductInquiry productInquiry = productInquiryRepository.findByIdWithProduct(inquiryId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        return new EditProductInquiryForm(productInquiry);
    }

    /**
     * 상품문의 수정
     *
     * @throws NoSuchProductException
     * @throws NoSuchBoardException
     */
    public void editInquiry(EditProductInquiryForm form, Long inquiryId) {
        //Product 엔티티 조회
        Product product = productRepository.findById(form.getProductId()).orElseThrow(() -> new NoSuchProductException("존재하지 않는 상품입니다."));

        //ProductInquiry 조회
        ProductInquiry productInquiry = productInquiryRepository.findByIdWithProduct(inquiryId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        //수정
        productInquiry.update(form.getTitle(), form.getContent(), product);
    }

    /**
     * 상품문의 삭제
     *
     * @throws NoSuchBoardException
     */
    public void deleteInquiry(Long qnaId) {
        //ProductInquiry 조회
        ProductInquiry productInquiry = productInquiryRepository.findById(qnaId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글 입니다."));

        //ProductInquiry 삭제
        productInquiryRepository.delete(productInquiry);
    }
}
