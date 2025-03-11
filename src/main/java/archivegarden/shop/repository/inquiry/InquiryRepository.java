package archivegarden.shop.repository.inquiry;

import archivegarden.shop.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, UserInquiryRepositoryCustom {

}
