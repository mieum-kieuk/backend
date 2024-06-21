package archivegarden.shop.repository.productInquiry;

import archivegarden.shop.entity.ProductInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductInquiryRepository extends JpaRepository<ProductInquiry, Long>, ProductInquiryRepositoryCustom {

    @Query("select i from ProductInquiry i join fetch i.product where i.id = :inquiryId")
    Optional<ProductInquiry> findByIdWithProduct(@Param("inquiryId") Long inquiryId);
}
