package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    public AuthUserResult execute(String usernameOrEmail) {

        // 1. Validate input
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            return null;
        }

        // 2. Normalize input
        String clean = usernameOrEmail.trim().toLowerCase(Locale.ROOT);

        // 3. Find user by username or email
        User user = userRepository.findByUsername(clean)
                .or(() -> userRepository.findByEmail(clean))
                .orElse(null);

        // 4. Validate user
        if (user == null || user.isDeleted()) {
            return null;
        }

        return toAuthUserResult(user);
    }

    public AuthUserResult execute(UUID userUuid) {
        return userRepository.findByUuid(userUuid)
                .filter(user -> !user.isDeleted())
                .map(this::toAuthUserResult)
                .orElse(null);
    }

    private AuthUserResult toAuthUserResult(User user) {
        var authoritiesResult = getUserAuthoritiesUseCase.execute(user.getUuid());
        Set<String> allAuthorities = new HashSet<>();
        if (authoritiesResult != null) {
            if (authoritiesResult.roles() != null) {
                allAuthorities.addAll(authoritiesResult.roles());
            }
            if (authoritiesResult.permissions() != null) {
                allAuthorities.addAll(authoritiesResult.permissions());
            }
        }
        return new AuthUserResult(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                allAuthorities
        );
    }
}
