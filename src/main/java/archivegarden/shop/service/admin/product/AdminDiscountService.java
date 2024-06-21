package archivegarden.shop.service.admin.promotion.discount;

import archivegarden.shop.dto.admin.AdminSearchForm;
import archivegarden.shop.dto.admin.product.discount.AddDiscountForm;
import archivegarden.shop.dto.admin.product.discount.DiscountDetailsDto;
import archivegarden.shop.dto.admin.product.discount.DiscountListDto;
import archivegarden.shop.dto.admin.product.discount.EditDiscountForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.exception.admin.AdminNotFoundException;
import archivegarden.shop.exception.ajax.AjaxNotFoundException;
import archivegarden.shop.repository.discount.AdminDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDiscountService {

    private final AdminDiscountRepository discountRepository;

    /**
     * 할인 저장
     */
    public Long saveDiscount(AddDiscountForm form) {
        //Discount 생성
        Discount discount = Discount.createDiscount(form);

        //Discount 저장
        discountRepository.save(discount);

        return discount.getId();
    }

    /**
     * 할인 단건 조회
     *
     * @throws AdminNotFoundException
     */
    @Transactional(readOnly = true)
    public DiscountDetailsDto getDiscount(Long discountId) {
        //Discount 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품 할인입니다."));

        return new DiscountDetailsDto(discount);
    }

    /**
     * 할인 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<DiscountListDto> getDiscountList(AdminSearchForm form, Pageable pageable) {
        return discountRepository.findAll(form, pageable).map(d -> new DiscountListDto(d));
    }

    /**
     * 할인 수정 폼 조회
     *
     * @throws
     */
    @Transactional(readOnly = true)
    public EditDiscountForm getEditDiscountForm(Long discountId) {
        //Discount 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 상품 할인입니다."));

        return new EditDiscountForm(discount);
    }

    /**
     * 할인 수정
     *
     * @throws AdminNotFoundException
     */
    public void updateDiscount(Long discountId, EditDiscountForm form) {
        //Discount 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AdminNotFoundException("존재하지 않는 할인 혜택입니다."));

        //할인 혜택 수정
        discount.update(form);
    }

    /**
     * Ajax: 할인 단건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteDiscount(Long discountId) {
        //Discount 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 할인입니다."));

        //Discount 삭제
        discountRepository.delete(discount);
    }

    /**
     * Ajax: 할인 여러건 삭제
     *
     * @throws AjaxNotFoundException
     */
    public void deleteDiscounts(List<Long> discountIds) {
        discountIds.stream().forEach((discountId) -> {
            //Discount 조회
            Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new AjaxNotFoundException("존재하지 않는 할인입니다."));

            //Discount 삭제
            discountRepository.delete(discount);
        });
    }

    /**
     * 상품 카테고리에 보여줄 할인 목록 조회
     * ex) [20%] 봄 신상 할인 (시작일시 ~ 종료일시)
     */
    @Transactional(readOnly = true)
    public Map<Long, String> getNewDiscounts() {
        Map<Long, String> result = new HashMap<>();
        List<Discount> discounts = discountRepository.findNewDiscounts();
        for (Discount discount : discounts) {
            String startedAtFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(discount.getStartedAt());
            String expiredAtFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(discount.getExpiredAt());
            String discountName = "[" + discount.getDiscountPercent() + "%] " + discount.getName()
                    + " (" + startedAtFormat + " ~ " + expiredAtFormat + ")";
            result.put(discount.getId(), discountName);
        }

        return result;
    }
}
