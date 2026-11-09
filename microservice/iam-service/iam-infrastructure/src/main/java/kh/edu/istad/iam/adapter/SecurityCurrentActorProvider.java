package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.usecase.user.LoadUserForAuthenticationUseCase;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityCurrentActorProvider implements CurrentActorProvider {

    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    @Override
    public String getCurrentActorSub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated actor is available.");
        }

        try {
            return java.util.UUID.fromString(authentication.getName()).toString();
        } catch (IllegalArgumentException ignored) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                AuthUserResult user = loadUserForAuthenticationUseCase.execute(userDetails.getUsername());
                if (user != null) {
                    return user.uuid().toString();
                }
            }
            throw new IllegalStateException("The authenticated actor has no IAM user identifier.");
        }
    }
}
