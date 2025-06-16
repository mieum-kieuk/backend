package archivegarden.shop.security.annotation;

import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(
        value = "admin2",
        userDetailsServiceBeanName = "adminUserDetailsService"
)
public @interface WithMockAnonymous {
}
