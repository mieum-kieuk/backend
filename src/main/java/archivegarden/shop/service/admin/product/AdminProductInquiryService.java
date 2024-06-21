package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminListDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.repository.productInquiry.ProductInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductInquiryService {

    private final ProductInquiryRepository inquiryRepository;

    /**
     * 상품 문의 단건 조회
     */
    public ProductInquiryAdminDetailsDto getInquiry(Long inquiryId) {
        return inquiryRepository.findAdminDto(inquiryId);
    }

    /**
     * 상품 문의 여러건 조회
     */
    public Page<ProductInquiryAdminListDto> getInquiries(AdminSearchForm form, Pageable pageRequest) {
        return inquiryRepository.findAdminDtoAll(form, pageRequest);
    }
}
