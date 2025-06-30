package archivegarden.shop.security.handler;

import archivegarden.shop.security.service.AccountContext;
import archivegarden.shop.service.order.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MemberAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CartService cartService;

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        setDefaultTargetUrl("/");

        AccountContext accountContext = (AccountContext) authentication.getPrincipal();
        String loginId = accountContext.getUsername();
        int cartItemCount = cartService.getCartItemCount(loginId);
        HttpSession session = request.getSession();
        session.setAttribute("cartItemCount", cartItemCount);

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if(savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
