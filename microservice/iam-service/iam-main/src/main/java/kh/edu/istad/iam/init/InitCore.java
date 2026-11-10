//package kh.edu.istad.iam.init;
//
//import jakarta.annotation.PostConstruct;
//import kh.edu.istad.iam.domain.core.entity.Permission;
//import kh.edu.istad.iam.domain.core.entity.Role;
//import kh.edu.istad.iam.domain.core.entity.User;
//import kh.edu.istad.iam.persistence.repository.PermissionJpaRepository;
//import kh.edu.istad.iam.persistence.repository.RoleJpaRepository;
//import kh.edu.istad.iam.persistence.repository.UserJpaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Set;
//
//@Configuration
//@RequiredArgsConstructor
//public class InitCore {
//
//    private final PermissionJpaRepository permissionJpaRepository;
//    private final RoleJpaRepository roleJpaRepository;
//    private final UserJpaRepository userJpaRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init(){
//        Permission permission = Permission.builder()
//                .permission("user:read:all")
//                .description("Read all users")
//                .build();
//
//        Role role = Role.builder()
//                .role("ADMIN")
//                .description("Admin ng hz")
//                .build();
//
//        User user = User.builder()
//                .familyName("Kung")
//                .givenName("Sovannda")
//                .email("kungsovannda@gmail.com")
//                .gender("Male")
//                .emailVerified(true)
//                .username("kungsovannda")
//                .password(passwordEncoder.encode("supersecret"))
//                .build();
//
//        if(!userJpaRepository.existsByUsername("kungsovannda")){
//            role = roleJpaRepository.save(role);
//            permission = permissionJpaRepository.save(permission);
//            user.setRoles(Set.of(role));
//            user.setPermissions(Set.of(permission));
//            userJpaRepository.save(user);
//        }
//    }
//}
