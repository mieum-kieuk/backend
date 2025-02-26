package archivegarden.shop.service.admin.product;

import archivegarden.shop.dto.admin.AdminSearchCondition;
import archivegarden.shop.dto.admin.product.discount.AdminAddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.AdminDiscountDetailsDto;
import archivegarden.shop.dto.admin.product.discount.AdminDiscountListDto;
import archivegarden.shop.dto.admin.product.discount.AdminEditDiscountForm;
import archivegarden.shop.dto.admin.product.product.AdminProductSummaryDto;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.Product;
import archivegarden.shop.exception.ajax.AjaxEntityNotFoundException;
import archivegarden.shop.exception.common.EntityNotFoundException;
import archivegarden.shop.repository.discount.DiscountRepository;
import archivegarden.shop.repository.product.ProductRepository;
import archivegarden.shop.service.admin.upload.AdminProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDiscountService {

    private final AdminProductImageService productImageService;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    /**
     * 할인 저장
     */
    public Long saveDiscount(AdminAddDiscountForm form) {
        List<Product> products = productRepository.findAll(form.getProductIds());
        Discount discount = Discount.createDiscount(form, products);
        discountRepository.save(discount);
        return discount.getId();
    }

    /**
     * 할인 단건 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminDiscountDetailsDto getDiscount(Long discountId) {
        Discount discount = discountRepository.findByIdWithProducts(discountId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 할인입니다."));

        AdminDiscountDetailsDto adminDiscountDetailsDto = new AdminDiscountDetailsDto(discount);
        List<AdminProductSummaryDto> products = adminDiscountDetailsDto.getProducts();

        for (AdminProductSummaryDto product : products) {
            String encodedDisplayImageData = productImageService.getEncodedImageData(product.getDisplayImageData());
            product.setDisplayImageData(encodedDisplayImageData);
        }

        return adminDiscountDetailsDto;
    }

    /**
     * 할인 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<AdminDiscountListDto> getDiscountList(AdminSearchCondition condition, Pageable pageable) {
        return discountRepository.findAll(condition, pageable).map(d -> new AdminDiscountListDto(d));
    }

    /**
     * 할인 수정 폼 조회
     *
     * @throws EntityNotFoundException
     */
    @Transactional(readOnly = true)
    public AdminEditDiscountForm getEditDiscountForm(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품 할인입니다."));
        return new AdminEditDiscountForm(discount);
    }

    /**
     * 할인 수정
     *
     * @throws EntityNotFoundException
     */
    public void updateDiscount(Long discountId, AdminEditDiscountForm form) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 할인 혜택입니다."));
        discount.update(form);
    }

    /**
     * Ajax: 할인 단건 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 할인입니다."));
        discountRepository.delete(discount);
    }

    /**
     * Ajax: 할인 여러건 삭제
     *
     * @throws AjaxEntityNotFoundException
     */
    public void deleteDiscounts(List<Long> discountIds) {
        discountIds.stream().forEach((discountId) -> {
            Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AjaxEntityNotFoundException("존재하지 않는 할인입니다."));
            discountRepository.delete(discount);
        });
    }
}
