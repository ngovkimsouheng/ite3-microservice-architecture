package kh.edu.istad.iam.config;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    @EventListener({AuthenticationSuccessEvent.class, InteractiveAuthenticationSuccessEvent.class})
    public void onAuthenticationSuccess(Object event) {
        Authentication auth = null;
        if (event instanceof AuthenticationSuccessEvent successEvent) {
            auth = successEvent.getAuthentication();
        } else if (event instanceof InteractiveAuthenticationSuccessEvent interactiveEvent) {
            auth = interactiveEvent.getAuthentication();
        }

        if (auth == null || !auth.isAuthenticated()) return;

        // Skip internal/anonymous tokens
        if ("anonymousUser".equals(auth.getName())) return;

        HttpServletRequest request = getHttpServletRequest();
        String ip = getClientIp(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : "";
        String device = parseDevice(userAgent);
        String location = isLocalIp(ip) ? "Phnom Penh, Cambodia (Localhost)" : "Phnom Penh, Cambodia";

        String authMethod = "Password";
        if (auth instanceof OAuth2LoginAuthenticationToken oauth2Token) {
            String clientRegId = oauth2Token.getClientRegistration().getRegistrationId();
            authMethod = formatProvider(clientRegId);
        } else if (auth instanceof OAuth2AuthenticationToken oauth2Auth) {
            String clientRegId = oauth2Auth.getAuthorizedClientRegistrationId();
            authMethod = formatProvider(clientRegId);
        }

        UUID userUuid = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof IamUserDetails iamUser) {
            userUuid = iamUser.getUuid();
        }

        try {
            recordSecurityEventUseCase.execute(
                    userUuid,
                    auth.getName(),
                    "LOGIN",
                    "LOGIN_SUCCESS",
                    "SUCCESS",
                    ip,
                    device,
                    location,
                    authMethod,
                    "Successful login via " + authMethod,
                    null
            );
        } catch (Exception ex) {
            log.error("Failed to record login success event: {}", ex.getMessage());
        }
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        Authentication auth = event.getAuthentication();
        String attemptedUsername = auth != null ? auth.getName() : "Unknown";

        // Don't record empty attempts
        if (attemptedUsername == null || attemptedUsername.isBlank()) return;

        HttpServletRequest request = getHttpServletRequest();
        String ip = getClientIp(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : "";
        String device = parseDevice(userAgent);
        String location = isLocalIp(ip) ? "Phnom Penh, Cambodia (Localhost)" : "Phnom Penh, Cambodia";

        String errorMsg = event.getException() != null ? event.getException().getMessage() : "Bad credentials";

        try {
            recordSecurityEventUseCase.execute(
                    null,
                    attemptedUsername,
                    "LOGIN",
                    "LOGIN_FAILED",
                    "FAILED",
                    ip,
                    device,
                    location,
                    "Password",
                    "Failed login attempt: " + errorMsg,
                    null
            );
        } catch (Exception ex) {
            log.error("Failed to record login failure event: {}", ex.getMessage());
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest();
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
            return xf.split(",")[0].trim();
        }
        String xr = request.getHeader("X-Real-IP");
        if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
            return xr.trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }

    private boolean isLocalIp(String ip) {
        return ip == null || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.startsWith("192.168.") || ip.startsWith("10.");
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "Web Browser / Desktop";

        String browser = "Browser";
        if (userAgent.contains("Edg/")) browser = "Edge";
        else if (userAgent.contains("Chrome/")) browser = "Chrome";
        else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) browser = "Safari";
        else if (userAgent.contains("Firefox/")) browser = "Firefox";
        else if (userAgent.contains("PostmanRuntime")) browser = "Postman";
        else if (userAgent.contains("curl")) browser = "cURL";

        String os = "Desktop";
        if (userAgent.contains("Windows NT 10.0")) os = "Windows 11/10";
        else if (userAgent.contains("Windows")) os = "Windows";
        else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) os = "iOS";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X")) os = "macOS";
        else if (userAgent.contains("Linux")) os = "Linux";

        return browser + " / " + os;
    }

    private String formatProvider(String providerId) {
        if (providerId == null) return "Social SSO";
        return switch (providerId.toLowerCase()) {
            case "google" -> "Google SSO";
            case "github" -> "GitHub SSO";
            case "facebook" -> "Facebook SSO";
            case "telegram" -> "Telegram";
            default -> providerId.substring(0, 1).toUpperCase() + providerId.substring(1) + " SSO";
        };
    }
}
