package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoadUserForAuthenticationUseCaseImpl implements LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    @Override
    public AuthUserResult execute(String usernameOrEmail) {
        String clean = usernameOrEmail.trim().toLowerCase();
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(clean))
                .orElse(null);

        if (user == null || user.isDeleted()) {
            return null;
        }

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