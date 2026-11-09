package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

        return new IamOidcUser(oidcUser, authUser);
    }

    private record IamOidcUser(OidcUser delegate, AuthUserResult authUser) implements OidcUser {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return CustomOAuth2UserService.authorities(authUser);
        }

        @Override
        public Map<String, Object> getAttributes() {
            return delegate.getAttributes();
        }

        @Override
        public String getName() {
            return authUser.uuid().toString();
        }

        @Override
        public Map<String, Object> getClaims() {
            return delegate.getClaims();
        }

        @Override
        public org.springframework.security.oauth2.core.oidc.OidcUserInfo getUserInfo() {
            return delegate.getUserInfo();
        }

        @Override
        public org.springframework.security.oauth2.core.oidc.OidcIdToken getIdToken() {
            return delegate.getIdToken();
        }
    }
}