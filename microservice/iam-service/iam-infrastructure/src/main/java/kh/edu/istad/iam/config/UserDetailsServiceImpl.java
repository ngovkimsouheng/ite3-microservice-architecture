package kh.edu.istad.iam.config;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoadUserForAuthenticationUseCase loadUserUseCase;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        AuthUserResult authUser;
        try {
            authUser = loadUserUseCase.execute(usernameOrEmail);
        } catch (Exception ex) {
            throw new UsernameNotFoundException("Failed to load user: " + usernameOrEmail, ex);
        }

        if (authUser == null) {
            throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (authUser.authorities() != null) {
            for (String auth : authUser.authorities()) {
                authorities.add(new SimpleGrantedAuthority(auth));
            }
        }

        return User.builder()
                .username(authUser.username()) // Use authUser.username() for standard form login matching
                .password(authUser.passwordHash() != null ? authUser.passwordHash() : "")
                .disabled(!authUser.enabled())
                .accountExpired(!authUser.accountNonExpired())
                .accountLocked(!authUser.accountNonLocked())
                .credentialsExpired(!authUser.credentialsNonExpired())
                .authorities(authorities)
                .build();
    }
}