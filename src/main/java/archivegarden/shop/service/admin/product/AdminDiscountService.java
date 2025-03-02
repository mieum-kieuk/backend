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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDiscountService {

    private final AdminProductImageService productImageService;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final Executor customAsyncExecutor;

    /**
     * 할인 저장
     */
    public Long saveDiscount(AdminAddDiscountForm form) {
        List<Product> products = productRepository.findAllInAdmin(form.getProductIds());
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

        List<CompletableFuture<Void>> futures = products.stream()
                .map(product -> CompletableFuture.runAsync(() -> {
                    // 비동기적으로 이미지 다운로드
                    String encodedImageData = productImageService.getEncodedImageDataAsync(product.getDisplayImageData()).join();
                    product.setDisplayImageData(encodedImageData);  // 이미지 인코딩된 데이터 설정
                }, customAsyncExecutor))  // customAsyncExecutor 사용
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

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

    /**
     * Ajax: 할인명 중복 검사
     */
    public boolean isNameAvailable(String name) {
        return discountRepository.findByName(name).isEmpty();
    }
}
