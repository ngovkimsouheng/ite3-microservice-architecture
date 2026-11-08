package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentActorProvider implements CurrentActorProvider {

    @Override
    public String getCurrentActorSub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated actor is available.");
        }

        return authentication.getName();
    }
}
