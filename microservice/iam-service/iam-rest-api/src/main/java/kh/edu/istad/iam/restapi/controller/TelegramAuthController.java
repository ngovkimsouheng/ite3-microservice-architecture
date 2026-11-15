package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.domain.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.iam.domain.usecase.identity.VerifyTelegramLoginUseCase;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TelegramAuthController {

    private final VerifyTelegramLoginUseCase verifyTelegramLoginUseCase;
    private final FindOrProvisionFederatedUserUseCase federatedUserUseCase;
    private final LinkIdentityUseCase linkIdentityUseCase;
    private final CurrentActorProvider currentActorProvider;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    @Value("${app.telegram.bot-token:}")
    private String botToken;

    @Value("${app.telegram.bot-username:}")
    private String botUsername;

    @GetMapping("/api/auth/telegram/config")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getTelegramConfig() {
        return ResponseEntity.ok(Map.of(
                "botUsername", botUsername != null ? botUsername : ""
        ));
    }

    @GetMapping("/api/auth/telegram/prepare-link")
    @ResponseBody
    public ResponseEntity<Void> prepareLink(HttpServletRequest request) {
        String currentSub = currentActorProvider.getCurrentActorSub();
        String linkValue = "TELEGRAM:" + System.currentTimeMillis() + ":" + currentSub;
        HttpSession session = request.getSession(true);
        session.setAttribute("iam.link-provider", linkValue);
        log.info("Prepared Telegram linking session for user: {}", currentSub);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/auth/telegram/callback")
    public void handleTelegramCallback(
            @RequestParam Map<String, String> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (botToken == null || botToken.isBlank()) {
            log.error("Telegram bot token is not configured in app.telegram.bot-token.");
            response.sendRedirect("/login?error=telegram_not_configured");
            return;
        }

        try {
            // Verify Telegram cryptographic signature and anti-replay
            FederatedIdentityCommand command = verifyTelegramLoginUseCase.execute(params, botToken);

            HttpSession session = request.getSession(false);
            String linkRequest = session != null ? (String) session.getAttribute("iam.link-provider") : null;

            if (linkRequest != null && linkRequest.startsWith("TELEGRAM:")) {
                // ACCOUNT LINKING FLOW
                session.removeAttribute("iam.link-provider");
                String[] parts = linkRequest.split(":", 3);
                UUID targetUserUuid;
                if (parts.length >= 3 && !parts[2].isBlank()) {
                    targetUserUuid = UUID.fromString(parts[2]);
                } else {
                    targetUserUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
                }

                linkIdentityUseCase.execute(targetUserUuid, command);
                log.info("Successfully linked Telegram user {} to IAM user {}", command.providerSubject(), targetUserUuid);

                try {
                    recordSecurityEventUseCase.execute(
                            targetUserUuid,
                            null,
                            "ACTIVITY",
                            "IDENTITY_LINKED",
                            "SUCCESS",
                            extractClientIp(request),
                            parseDevice(request.getHeader("User-Agent")),
                            "Phnom Penh, Cambodia",
                            "Telegram",
                            "Telegram Account Linked",
                            "Connected Telegram messenger account (@" + (command.username() != null ? command.username() : command.providerSubject()) + ")"
                    );
                } catch (Exception ignored) {}

                response.sendRedirect("/account/linked-accounts?linked=telegram");
            } else {
                // LOGIN FLOW
                AuthUserResult authUser = federatedUserUseCase.execute(command);

                Set<GrantedAuthority> authorities = new HashSet<>();
                if (authUser.authorities() != null) {
                    for (String auth : authUser.authorities()) {
                        authorities.add(new SimpleGrantedAuthority(auth));
                    }
                }
                if (authorities.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(authUser.username())
                        .password("")
                        .disabled(!authUser.enabled())
                        .accountExpired(!authUser.accountNonExpired())
                        .accountLocked(!authUser.accountNonLocked())
                        .credentialsExpired(!authUser.credentialsNonExpired())
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

                try {
                    recordSecurityEventUseCase.execute(
                            authUser.uuid(),
                            authUser.username(),
                            "LOGIN",
                            "LOGIN_SUCCESS",
                            "SUCCESS",
                            extractClientIp(request),
                            parseDevice(request.getHeader("User-Agent")),
                            "Phnom Penh, Cambodia",
                            "Telegram",
                            "Successful login via Telegram",
                            null
                    );
                } catch (Exception ignored) {}

                log.info("Telegram login successful for user: {}", authUser.username());
                response.sendRedirect("/account/profile");
            }
        } catch (Exception ex) {
            log.error("Telegram authentication failed: {}", ex.getMessage());
            response.sendRedirect("/login?error=telegram_failed");
        }
    }

    private String extractClientIp(HttpServletRequest request) {
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

    private String parseDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "Web Browser / Desktop";

        String browser = "Browser";
        if (userAgent.contains("Edg/")) browser = "Edge";
        else if (userAgent.contains("Chrome/")) browser = "Chrome";
        else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) browser = "Safari";
        else if (userAgent.contains("Firefox/")) browser = "Firefox";

        String os = "Desktop";
        if (userAgent.contains("Windows")) os = "Windows";
        else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) os = "iOS";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X")) os = "macOS";
        else if (userAgent.contains("Linux")) os = "Linux";

        return browser + " / " + os;
    }
}
