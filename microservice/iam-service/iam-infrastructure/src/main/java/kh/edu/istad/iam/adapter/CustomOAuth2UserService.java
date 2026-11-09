package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final FederatedUserProvisioner federatedUserProvisioner;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        AuthUserResult authUser = federatedUserProvisioner.provision(
                userRequest, oAuth2User.getAttributes(), oAuth2User.getName());

        return new DefaultOAuth2User(
                authorities(authUser),
                Map.of(
                        "sub", authUser.uuid().toString(),
                        "name", authUser.username() != null ? authUser.username() : authUser.uuid().toString()
                ),
                "sub"
        );
    }

    static Set<GrantedAuthority> authorities(AuthUserResult authUser) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (authUser.authorities() != null) {
            for (String auth : authUser.authorities()) {
                authorities.add(new SimpleGrantedAuthority(auth));
            }
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}