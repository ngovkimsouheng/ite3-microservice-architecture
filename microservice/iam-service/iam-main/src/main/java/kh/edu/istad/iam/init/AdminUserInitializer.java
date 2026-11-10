package kh.edu.istad.iam.init;

import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Bootstraps the initial system administrator user and assigns the default ADMIN role.
 * Role permissions are automatically inherited through the system ADMIN role definition.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String SYSTEM_ACTOR = "SYSTEM";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:kungsovannda}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.email:kungsovannda@gmail.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:supersecret}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.given-name:Sovannda}")
    private String adminGivenName;

    @Value("${app.bootstrap.admin.family-name:Kung}")
    private String adminFamilyName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Admin user '{}' already exists. Skipping initialization.", adminUsername);
            return;
        }

        Role adminRole = roleRepository.findByName(ADMIN_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException(
                        "Required system role '" + ADMIN_ROLE_NAME + "' not found. Ensure Liquibase migration has executed."));

        OffsetDateTime now = OffsetDateTime.now();

        User adminUser = User.builder()
                .uuid(UUID.randomUUID())
                .username(adminUsername)
                .email(adminEmail)
                .emailVerified(true)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .givenName(adminGivenName)
                .familyName(adminFamilyName)
                .gender("Male")
                .locale("en-US")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(SYSTEM_ACTOR)
                .updatedAt(now)
                .updatedBy(SYSTEM_ACTOR)
                .version(0L)
                .build();

        User savedUser = userRepository.save(adminUser);
        log.info("Created system admin user '{}' with UUID: {}", savedUser.getUsername(), savedUser.getUuid());

        UserRoleAssignment roleAssignment = UserRoleAssignment.builder()
                .userId(savedUser.getId())
                .roleId(adminRole.getId())
                .assignedAt(now)
                .assignedBy(SYSTEM_ACTOR)
                .reason("Initial system administrator bootstrap")
                .build();

        userRepository.saveRoleAssignment(roleAssignment);
        log.info("Assigned role '{}' to admin user '{}'. All system permissions are inherited.",
                ADMIN_ROLE_NAME, savedUser.getUsername());
    }
}