package archivegarden.shop.security.manager;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminAuthenticationManager implements AuthenticationManager {

    private List<AuthenticationProvider> providers = Collections.emptyList();

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private boolean eraseCredentialsAfterAuthentication = true;

    public AdminAuthenticationManager(AuthenticationProvider... providers) {
        this(Arrays.asList(providers));
    }

    public AdminAuthenticationManager(List<AuthenticationProvider> providers) {
        Assert.notNull(providers, "providers list cannot be null");
        this.providers = providers;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;

        for (AuthenticationProvider provider : getProviders()) {
            if (!provider.supports(toTest)) {
                continue;
            }

            try {
                result = provider.authenticate(authentication);
                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            } catch (AccountStatusException | InternalAuthenticationServiceException ex) {
                throw ex;
            } catch (AuthenticationException ex) {
                lastException = ex;
            }
        }

        if (result != null) {
            if (this.eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)) {
                ((CredentialsContainer) result).eraseCredentials();
            }

            return result;
        }

        if (lastException == null) {
            lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound",
                    new Object[] { toTest.getName() }, "No AuthenticationProvider found for {0}"));
        }

        throw lastException;
    }

    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken token) && (dest.getDetails() == null)) {
            token.setDetails(source.getDetails());
        }
    }

    public List<AuthenticationProvider> getProviders() {
        return providers;
    }
}
