package archivegarden.shop.service.admin.promotion.discount;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import archivegarden.shop.entity.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDiscountServiceTest {

    @Mock
    AdminDiscountService adminDiscountService;

    @Test
    @DisplayName("할인 혜택 등록")
    public void registerDiscount() {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.FIX, 1000);
        when(adminDiscountService.registerDiscount(form)).thenReturn(1L);

        //when
        Long discountId = adminDiscountService.registerDiscount(form);

        //then
        assertThat(discountId).isEqualTo(1L);
    }
}