package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.user.community.inquiry.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserInquiryRepositoryCustom {

    Optional<InquiryDetailsDto> findInquiry(Long inquiryId);

    Page<InquiryListDto> findInquiries(Pageable pageable);

    Optional<EditInquiryForm> findInquiryForEdit(Long inquiryId);

    Page<ProductPageInquiryListDto> findInquiriesByProductId(Long productId, Pageable pageable);

    Page<MyInquiryListDto> findMyInquiries(Long memberId, Pageable pageable);
}