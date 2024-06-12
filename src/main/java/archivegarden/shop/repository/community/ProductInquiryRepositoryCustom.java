package archivegarden.shop.repository.community;

import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductInquiryRepositoryCustom {

    ProductInquiryDetailsDto findDto(Long inquiryId);

    Page<ProductInquiryListDto> findDtoAll(Pageable pageable);
}
