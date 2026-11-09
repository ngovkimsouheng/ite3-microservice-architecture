package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final FederatedUserProvisioner federatedUserProvisioner;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        AuthUserResult authUser = federatedUserProvisioner.provision(
                userRequest,
                oidcUser.getAttributes(),
                oidcUser.getName()
        );

        Map<String, Object> claims = new HashMap<>(oidcUser.getIdToken().getClaims());
        claims.put("sub", authUser.uuid().toString());
        claims.put("name", authUser.username() != null ? authUser.username() : authUser.uuid().toString());

        OidcIdToken idToken = new OidcIdToken(
                oidcUser.getIdToken().getTokenValue(),
                oidcUser.getIdToken().getIssuedAt(),
                oidcUser.getIdToken().getExpiresAt(),
                claims
        );

        OidcUserInfo userInfo = oidcUser.getUserInfo() != null
                ? new OidcUserInfo(new HashMap<>(oidcUser.getUserInfo().getClaims()))
                : null;

        return new DefaultOidcUser(
                CustomOAuth2UserService.authorities(authUser),
                idToken,
                userInfo,
                "sub"
        );
    }
}