package archivegarden.shop.repository.answer;

import archivegarden.shop.exception.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("select a from Answer a join fetch a.admin where a.productInquiry.id = :inquiryId")
    Optional<Answer> findByProductInquiryId(@Param("inquiryId") Long inquiryId);
}
