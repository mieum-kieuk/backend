package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import archivegarden.shop.dto.user.community.inquiry.InquiryDetailsDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListDto;
import archivegarden.shop.dto.user.community.inquiry.InquiryListInProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInquiryRepositoryCustom {

    InquiryDetailsDto findInquiry(Long inquiryId);

    Page<InquiryListDto> findInquiries(Pageable pageable);

    Page<InquiryListInProductDto> findInquiriesByProductId(Long productId, Pageable pageable);

    EditInquiryForm findInquiryForEdit(Long inquiryId);
}