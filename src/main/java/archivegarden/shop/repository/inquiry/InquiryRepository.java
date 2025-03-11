package archivegarden.shop.repository.inquiry;

import archivegarden.shop.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, UserInquiryRepositoryCustom {

    @Query("select i from Inquiry i join fetch i.product where i.id = :inquiryId")
    Optional<Inquiry> findByIdWithProduct(@Param("inquiryId") Long inquiryId);
}
