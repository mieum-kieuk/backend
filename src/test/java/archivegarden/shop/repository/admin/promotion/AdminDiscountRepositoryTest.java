package archivegarden.shop.repository.admin.promotion;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import archivegarden.shop.entity.Discount;
import archivegarden.shop.entity.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AdminDiscountRepositoryTest {

    @Autowired
    AdminDiscountRepository adminDiscountRepository;

    @Test
    @DisplayName("할인 혜택 등록")
    public void saveDiscount() {
        //given
        AddDiscountForm addDiscountForm = new AddDiscountForm(DiscountType.RATE, 10);
        Discount discount = Discount.createDiscount(addDiscountForm);

        //when
        Discount savedDiscount = adminDiscountRepository.save(discount);

        //then
        assertThat(discount).isSameAs(savedDiscount);
        assertThat(discount.getValue()).isEqualTo(savedDiscount.getValue());
        assertThat(adminDiscountRepository.count()).isEqualTo(1);
    }
}