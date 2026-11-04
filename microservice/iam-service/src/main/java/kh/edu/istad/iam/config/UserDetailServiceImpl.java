package kh.edu.istad.iam.config;

import kh.edu.istad.iam.features.user.UserjpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserjpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        var existing = userRepository.findByUsernameOrEmail(username, username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//     Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
//     existing.getPermissions().forEach(permission -> {
//         grantedAuthorities.add((GrantedAuthority) permission::getPermission);
        return null;

    }



}
