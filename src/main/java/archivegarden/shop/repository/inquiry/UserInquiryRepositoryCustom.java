package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.user.community.inquiry.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInquiryRepositoryCustom {

    InquiryDetailsDto findInquiry(Long inquiryId);

    Page<InquiryListDto> findInquiries(Pageable pageable);

    Page<ProductPageInquiryListDto> findInquiriesByProductId(Long productId, Pageable pageable);

    Page<MyPageInquiryListDto> findMyInquiries(Long memberId, Pageable pageable);

    EditInquiryForm findInquiryForEdit(Long inquiryId);
}