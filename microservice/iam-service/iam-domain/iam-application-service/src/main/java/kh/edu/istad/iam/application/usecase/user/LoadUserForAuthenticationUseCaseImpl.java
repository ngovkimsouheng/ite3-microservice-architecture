
package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoadUserForAuthenticationUseCaseImpl
        implements LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    @Override
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

        // 5. Load user authorities
        var authoritiesResult =
                getUserAuthoritiesUseCase.execute(user.getUuid());

        Set<String> allAuthorities = new HashSet<>();

        if (authoritiesResult != null) {
            if (authoritiesResult.roles() != null) {
                allAuthorities.addAll(authoritiesResult.roles());
            }

            if (authoritiesResult.permissions() != null) {
                allAuthorities.addAll(authoritiesResult.permissions());
            }
        }

        // 6. Return authentication result
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
