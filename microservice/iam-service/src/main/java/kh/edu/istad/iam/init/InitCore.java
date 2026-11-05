package kh.edu.istad.iam.init;

import jakarta.annotation.PostConstruct;
import kh.edu.istad.iam.domain.Permission;
import kh.edu.istad.iam.domain.Role;
import kh.edu.istad.iam.domain.User;
import kh.edu.istad.iam.feature.permission.PermissionJpaRepository;
import kh.edu.istad.iam.feature.role.Role_JpaRepository;
import kh.edu.istad.iam.feature.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitCore {
    private final UserJpaRepository userRepository;
    private final Role_JpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Initialize core data here
        Permission permission = Permission.builder()
                .permission("read")
                .description("Read all users")
                .build();
        Role role = Role.builder()
                .role("admin")
                .description("Admin ng hx")
                .build();
        User user = User.builder()
                .familyName("kim")
                .givenName("kim")
                .email("souheng@gmail.com")
                .emailVerified(true)
                .gender("male")
                .username("souheng")
                .password(passwordEncoder.encode("qwer"))
                .build();
        if (!userRepository.existsByUsername("souheng")) {
            role = roleRepository.save(role);
            permission = permissionRepository.save(permission);

            user.setRoles(Set.of(role));
            user.setPermissions(Set.of(permission));

            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            userRepository.save(user);
        }
//        if (userRepository.existsByUsername("souheng")) {
//            role = roleRepository.save(role);
//            permission = permissionRepository.save(permission);
//
//            user.setRoles(Set.of(role));
//            user.setPermissions(Set.of(permission));
//            userRepository.save(user);
//        }

    }


}
