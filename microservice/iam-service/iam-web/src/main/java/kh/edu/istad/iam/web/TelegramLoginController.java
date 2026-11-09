package kh.edu.istad.iam.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.domain.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.iam.domain.usecase.identity.VerifyTelegramLoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TelegramLoginController {

    private final VerifyTelegramLoginUseCase verifyTelegramLoginUseCase;
    private final FindOrProvisionFederatedUserUseCase federatedUserUseCase;
    private final LinkIdentityUseCase linkIdentityUseCase;
    private final CurrentActorProvider currentActorProvider;

    @Value("${app.telegram.bot-token:}")
    private String botToken;

    @GetMapping("/login/telegram/callback")
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        String linkRequest = consumeLinkRequest(request.getSession(false));
        FederatedIdentityCommand command = verifyTelegramLoginUseCase.execute(parameters(request), botToken);
        if (linkRequest != null) {
            if (!isValidLinkRequest(linkRequest)) {
                throw new IllegalArgumentException("The identity-link request is invalid or expired.");
            }
            linkIdentityUseCase.execute(
                    UUID.fromString(currentActorProvider.getCurrentActorSub()),
                    command
            );
            return "redirect:/profile/accounts";
        }

        AuthUserResult user = federatedUserUseCase.execute(command);
        var authorities = user.authorities().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.uuid().toString(), null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
        return "redirect:/";
    }

    private Map<String, String> parameters(HttpServletRequest request) {
        if (request.getParameterMap().size() > 20) {
            throw new IllegalArgumentException("Too many Telegram login parameters.");
        }
        Map<String, String> parameters = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values == null || values.length != 1 || values[0] == null) {
                throw new IllegalArgumentException("Invalid Telegram login parameters.");
            }
            parameters.put(key, values[0]);
        });
        return parameters;
    }

    private String consumeLinkRequest(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute("iam.link-provider");
        session.removeAttribute("iam.link-provider");
        return value instanceof String linkRequest ? linkRequest : null;
    }

    private boolean isValidLinkRequest(String linkRequest) {
        String[] parts = linkRequest.split(":", 2);
        if (parts.length != 2 || !"TELEGRAM".equals(parts[0])) {
            return false;
        }
        try {
            long ageMillis = System.currentTimeMillis() - Long.parseLong(parts[1]);
            return ageMillis >= 0 && ageMillis <= 5 * 60 * 1000L;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
