package archivegarden.shop.repository.inquiry;

import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryDetailsDto;
import archivegarden.shop.dto.admin.product.inquiry.AdminInquiryListDto;
import archivegarden.shop.dto.admin.product.product.AdminProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminInquiryRepositoryCustom {

    Optional<AdminInquiryDetailsDto> findInquiryInAdmin(Long inquiryId);

    Page<AdminInquiryListDto> findInquiriesInAdmin(AdminProductSearchCondition cond, Pageable pageable);
}
