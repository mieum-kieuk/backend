package archivegarden.shop.controller.admin.promotion;

import archivegarden.shop.dto.admin.discount.AddDiscountForm;
import archivegarden.shop.entity.DiscountType;
import archivegarden.shop.service.admin.promotion.discount.AdminDiscountService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(AdminDiscountController.class)
@ExtendWith(MockitoExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminDiscountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AdminDiscountService adminDiscountService;

    private Validator validator;

    @BeforeEach
    void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("정액 할인 혜택 등록")
    void addFixDiscount() throws Exception {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.FIX, 1000);

        //when, then
        mockMvc.perform(post("/admin/promotion/discount/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/promotion/discount/**"));
    }

    @Test
    @DisplayName("정액 할인 혜택 - 검증 실패 value < 1")
    public void addFixDiscount_valid() throws Exception {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.FIX, -10);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/admin/promotion/discount/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form)
                        .with(csrf()))
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) model.get("org.springframework.validation.BindingResult.form");
        String message = bindingResult.getFieldError("value").getDefaultMessage();

        //then
        assertThat(message).isEqualTo("1 이상의 값을 입력해 주세요.");
    }

    @Test
    @DisplayName("할인 혜택 등록 - 검증 실패 value = null")
    public void addDiscount_valid_value() {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.FIX, null);

        //when
        Set<ConstraintViolation<AddDiscountForm>> violations = validator.validate(form);
        Iterator<ConstraintViolation<AddDiscountForm>> iterator = violations.iterator();
        List<String> messages = new ArrayList<>();

        while (iterator.hasNext()) {
            ConstraintViolation<AddDiscountForm> next = iterator.next();
            messages.add(next.getMessage());
        }

        //then
        assertThat(messages).contains("할인 설정을 입력해 주세요.");
    }

    @Test
    @DisplayName("정률 할인 혜택 등록")
    public void addRateDiscount() throws Exception {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.RATE, 10);

        //when, then
        mockMvc.perform(post("/admin/promotion/discount/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/promotion/discount/**"));
    }

    @Test
    @DisplayName("정률 할인 혜택 등록 - 검증 실패 value > 100")
    public void addRateDiscount_valid1() throws Exception {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.RATE, 200);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/admin/promotion/discount/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form)
                        .with(csrf()))
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) model.get("org.springframework.validation.BindingResult.form");
        String message = bindingResult.getFieldError("value").getDefaultMessage();

        //then
        assertThat(message).isEqualTo("1부터 100사이의 값을 입력해 주세요.");
    }

    @Test
    @DisplayName("정률 할인 혜택 등록 - 검증 실패 value < 1")
    public void addRateDiscount_valid2() throws Exception {
        //given
        AddDiscountForm form = new AddDiscountForm(DiscountType.RATE, 0);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/admin/promotion/discount/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("form", form)
                        .with(csrf()))
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) model.get("org.springframework.validation.BindingResult.form");
        String message = bindingResult.getFieldError("value").getDefaultMessage();

        //then
        assertThat(message).isEqualTo("1부터 100사이의 값을 입력해 주세요.");
    }
}