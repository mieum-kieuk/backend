package archivegarden.shop.repository.user.community.inquiry;

import archivegarden.shop.dto.user.community.inquiry.InquiryDetailsDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {

    InquiryDetailsDto findInquiry(Long inquiryId);

    Page<InquiryListDto> findInquiries(Pageable pageable);

//    ProductInquiryAdminDetailsDto findAdminDto(Long inquiryId);

//    Page<ProductInquiryAdminListDto> findAdminDtoAll(AdminSearchCondition form, Pageable pageable);
}
