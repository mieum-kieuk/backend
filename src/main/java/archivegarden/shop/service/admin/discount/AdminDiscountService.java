package archivegarden.shop.service.admin.discount;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.repository.admin.AdminDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
