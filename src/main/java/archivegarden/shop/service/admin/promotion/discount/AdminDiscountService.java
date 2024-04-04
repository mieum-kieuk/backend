package archivegarden.shop.service.admin.promotion.discount;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.DiscountDetailsDto;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.exception.NoSuchDiscountException;
import archivegarden.shop.exception.ajax.NoSuchDiscountAjaxException;
import archivegarden.shop.repository.admin.promotion.AdminDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDiscountService {

    private final AdminDiscountRepository discountRepository;

    /**
     * 할인 혜택 저장
     */
    public Long saveDiscount(AddDiscountForm form) {
        //엔티티 생성
        Discount discount = Discount.createDiscount(form);

        //할인 혜택 저장
        discountRepository.save(discount);

        return discount.getId();
    }

    /**
     * 할인 혜택 단건 조회
     *
     * @throws NoSuchDiscountException discountId로 DB에서 데이터 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public DiscountDetailsDto getDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchDiscountException("존재하지 않는 할인 혜택입니다."));
        return new DiscountDetailsDto(discount);
    }

    /**
     * 할인 혜택 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<DiscountDetailsDto> getDiscountList(Pageable pageable) {
        return discountRepository.findAll(pageable).map(d -> new DiscountDetailsDto(d));
    }

    /**
     * 할인 혜택 수정 폼 조회
     */
    @Transactional(readOnly = true)
    public EditDiscountForm getEditDiscountForm(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchDiscountException("존재하지 않는 할인 혜택입니다."));
        return new EditDiscountForm(discount);
    }

    /**
     * 할인 혜택 수정
     *
     * @throws NoSuchDiscountException discoundId로 DB에서 데이터 찾을 수 없을 때
     */
    public void updateDiscount(Long discountId, EditDiscountForm form) {
        //엔티티 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchDiscountException("존재하지 않는 할인 혜택입니다."));

        //할인 혜택 수정
        discount.update(form);
    }

    /**
     * 할인 혜택 단건 삭제
     *
     * @throws NoSuchDiscountException discountId로 DB에서 데이터 찾을 수 없을 때
     */
    public void deleteDiscount(Long discountId) {
        //엔티티 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchDiscountException("존재하지 않는 할인 혜택입니다."));

        //할인 삭제
        discountRepository.delete(discount);
    }

    /**
     * Ajax
     * 할인 혜택 여러개 삭제
     *
     * @throws NoSuchDiscountAjaxException discountId로 DB에서 데이터 찾을 수 없을 때
     */
    public void deleteDiscounts(List<Long> discountIds) {
        discountIds.stream().forEach((discountId) -> {
            Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchDiscountAjaxException("존재하지 않는 할인 혜택입니다."));
            discountRepository.delete(discount);
        });
    }

    /**
     * 상품 카테고리에 보여줄 할인 목록 조회
     * ex) [20%] 봄 신상 할인
     */
    @Transactional(readOnly = true)
    public Map<Long, String> getDiscountNames() {
        Map<Long, String> result = new HashMap<>();
        List<Discount> discounts = discountRepository.findAll();
        for (Discount discount : discounts) {
            String discountName = "[" + discount.getDiscountPercent() + "%] " + discount.getName();
            result.put(discount.getId(), discountName);
        }

        return result;
    }
}
