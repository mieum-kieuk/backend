package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import archivegarden.shop.exception.common.EntityNotFoundException;
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

    /**
     * 상품 문의 단건 조회
     *
     * @throws EntityNotFoundException
     */
    public AdminInquiryDetailsDto getInquiry(Long inquiryId) {
        AdminInquiryDetailsDto inquiryDetailsDto = inquiryRepository.findInquiryInAdmin(inquiryId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 문의 글입니다."));
        String encodedImageData = productImageService.getEncodedImageData(inquiryDetailsDto.getProductImageData());
        inquiryDetailsDto.setProductImageData(encodedImageData);
        return inquiryDetailsDto;
    }

    /**
     * 상품 문의 여러건 조회
     */
    public Page<AdminInquiryListDto> getInquiries(AdminProductSearchCondition condition, Pageable pageRequest) {
        Page<AdminInquiryListDto> inquiryListDtos = inquiryRepository.findInquiriesInAdmin(condition, pageRequest);
        inquiryListDtos.forEach(i -> {
            String encodedImageData = productImageService.getEncodedImageData(i.getProductImageData());
            i.setProductImageData(encodedImageData);
        });
        return inquiryListDtos;
    }
}
