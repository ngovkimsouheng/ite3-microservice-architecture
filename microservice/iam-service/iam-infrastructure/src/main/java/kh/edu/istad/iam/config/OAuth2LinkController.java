package kh.edu.istad.iam.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuth2LinkController {

    private final CurrentActorProvider currentActorProvider;

    @GetMapping("/oauth2/link/{provider}")
    public void initiateLink(
            @PathVariable("provider") String provider,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.warn("Unauthenticated attempt to link provider {}", provider);
            response.sendRedirect("/login");
            return;
        }

        try {
            String currentSub = currentActorProvider.getCurrentActorSub();
            String linkValue = provider.toUpperCase(Locale.ROOT) + ":" + System.currentTimeMillis() + ":" + currentSub;
            HttpSession session = request.getSession(true);
            session.setAttribute("iam.link-provider", linkValue);
            log.info("Initiating identity link for user {} with provider {}", currentSub, provider);
            response.sendRedirect("/oauth2/authorization/" + provider.toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            log.error("Failed to initiate identity link for provider {}: {}", provider, ex.getMessage());
            response.sendRedirect("/account/linked-accounts?error=link_init_failed");
        }
    }
}
