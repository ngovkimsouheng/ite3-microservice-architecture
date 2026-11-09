package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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

        return withIamIdentity(oidcUser, authUser);
    }

    static OidcUser withIamIdentity(OidcUser oidcUser, AuthUserResult authUser) {
        Map<String, Object> userInfoClaims = new LinkedHashMap<>();
        if (oidcUser.getUserInfo() != null) {
            userInfoClaims.putAll(oidcUser.getUserInfo().getClaims());
        }
        userInfoClaims.putIfAbsent("sub", oidcUser.getIdToken().getSubject());
        userInfoClaims.put("iam_user_id", authUser.uuid().toString());

        return new DefaultOidcUser(
                CustomOAuth2UserService.authorities(authUser),
                oidcUser.getIdToken(),
                new OidcUserInfo(userInfoClaims),
                "iam_user_id"
        );
    }
}