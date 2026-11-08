package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.dto.CommonDtos.FederatedIdentityCommand;
import kh.edu.istad.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
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

    private final FindOrProvisionFederatedUserUseCase federatedUserUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerSubject;
        String email = null;
        boolean emailVerified = false;
        String displayName = null;
        String username = null;
        String pictureUrl = null;

        if ("github".equalsIgnoreCase(registrationId)) {
            providerSubject = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            emailVerified = email != null;
            displayName = (String) attributes.get("name");
            username = (String) attributes.get("login");
            pictureUrl = (String) attributes.get("avatar_url");
        } else if ("google".equalsIgnoreCase(registrationId)) {
            providerSubject = String.valueOf(attributes.get("sub"));
            email = (String) attributes.get("email");
            Object verified = attributes.get("email_verified");
            emailVerified = Boolean.TRUE.equals(verified);
            displayName = (String) attributes.get("name");
            pictureUrl = (String) attributes.get("picture");
        } else if ("facebook".equalsIgnoreCase(registrationId)) {
            providerSubject = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            emailVerified = email != null;
            displayName = (String) attributes.get("name");
        } else {
            providerSubject = oAuth2User.getName();
        }

        FederatedIdentityCommand command = new FederatedIdentityCommand(
                registrationId.toUpperCase(),
                providerSubject,
                email,
                emailVerified,
                displayName,
                username,
                pictureUrl,
                attributes
        );

        AuthUserResult authUser = federatedUserUseCase.execute(command);

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (authUser.authorities() != null) {
            for (String auth : authUser.authorities()) {
                authorities.add(new SimpleGrantedAuthority(auth));
            }
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new DefaultOAuth2User(
                authorities,
                Map.of(
                        "sub", authUser.uuid().toString(),
                        "name", authUser.username() != null ? authUser.username() : authUser.uuid().toString()
                ),
                "sub"
        );
    }
}
