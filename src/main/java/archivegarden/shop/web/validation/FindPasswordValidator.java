package archivegarden.shop.web.validation;

import archivegarden.shop.dto.member.FindPasswordForm;
import archivegarden.shop.entity.FindAccountType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class FindPasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FindPasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FindPasswordForm form = (FindPasswordForm) target;

        //아이디
        if (!StringUtils.hasText(form.getLoginId())) {
            errors.rejectValue("loginId", "required");
        } else if (!Pattern.matches("(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,20}+$", form.getLoginId())) {
            errors.rejectValue("loginId", "regex");
        }

        //이름
        if (!StringUtils.hasText(form.getName())) {
            errors.rejectValue("name", "required");
        }

        //이메일
        if (form.getFindType() == FindAccountType.EMAIL) {
            if (!StringUtils.hasText(form.getEmail())) {
                errors.rejectValue("email", "required");
            } else if (!Pattern.matches("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$", form.getEmail())) {
                errors.rejectValue("email", "regex");
            }
            //휴대전화번호
        } else {
            if (!Pattern.matches("^01(0|1|[6-9])$", form.getPhonenumber1()) ||
                    !Pattern.matches("^(\\d){3,4}$", form.getPhonenumber2()) ||
                    !Pattern.matches("^(\\d){4}$", form.getPhonenumber3())) {
                errors.rejectValue("phonenumber1", "regex");
            }
        }
    }
}
