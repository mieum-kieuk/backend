package archivegarden.shop.repository.inquiry;

import archivegarden.shop.entity.Answer;
import archivegarden.shop.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, UserInquiryRepositoryCustom, AdminInquiryRepositoryCustom {

    @Query("select i.answer from Inquiry i where i.id = :inquiryId")
    Optional<Answer> findAnswerByInquiryId(@Param("inquiryId") Long inquiryId);
}