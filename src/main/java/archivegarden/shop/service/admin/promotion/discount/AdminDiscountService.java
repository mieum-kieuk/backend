package archivegarden.shop.service.admin.promotion.discount;

import archivegarden.shop.dto.admin.promotion.AddDiscountForm;
import archivegarden.shop.dto.admin.promotion.DiscountDto;
import archivegarden.shop.dto.admin.promotion.EditDiscountForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.repository.admin.promotion.AdminDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDiscountService {

    private final AdminDiscountRepository discountRepository;

    /**
     * 할인 혜택 등록
     */
    public Long registerDiscount(AddDiscountForm form) {
        //엔티티 생성
        Discount discount = Discount.createDiscount(form);

        //할인 저장
        discountRepository.save(discount);

        return discount.getId();
    }

    /**
     * 할인 혜택 상세 페이지
     */
    public DiscountDto discountDetails(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 할인 혜택입니다."));
        return new DiscountDto(discount);
    }

    /**
     * 할인 혜택 수정
     */
    public void updateDiscount(Long discountId, EditDiscountForm form) {
        //엔티티 조회
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 할인 혜택입니다."));

        //할인 혜택 수정
        discount.update(form);
    }

    /**
     * 할인 혜택 삭제
     */
    public void deleteDiscount(Long discountId) {
        discountRepository.deleteById(discountId);
    }

    /**
     * 할인 혜택 단건 조회
     */
    public DiscountDto getDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 할인 혜택입니다."));
        return new DiscountDto(discount);
    }

    /**
     * 할인 혜택 목록 조회
     */
    public List<DiscountDto> getDiscountList() {
        return discountRepository.findAll()
                .stream()
                .map(d -> new DiscountDto(d))
                .collect(Collectors.toList());
    }
}
