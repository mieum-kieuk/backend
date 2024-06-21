package archivegarden.shop.repository.productInquiry;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.ProductInquiryAdminListDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryDetailsDto;
import archivegarden.shop.dto.community.inquiry.ProductInquiryListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductInquiryRepositoryCustom {

    ProductInquiryDetailsDto findDto(Long inquiryId);

    ProductInquiryAdminDetailsDto findAdminDto(Long inquiryId);

    Page<ProductInquiryListDto> findDtoAll(Pageable pageable);

    Page<ProductInquiryAdminListDto> findAdminDtoAll(AdminSearchForm form, Pageable pageable);
}
