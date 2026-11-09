package kh.edu.istad.iam.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class IamJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
        if (scopeAuthorities != null) {
            authorities.addAll(scopeAuthorities);
            scopeAuthorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("SCOPE_"))
                    .map(authority -> authority.substring("SCOPE_".length()))
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        addClaimAuthorities(jwt, "roles", authorities);
        addClaimAuthorities(jwt, "permissions", authorities);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private void addClaimAuthorities(Jwt jwt, String claimName, Set<GrantedAuthority> authorities) {
        Object claim = jwt.getClaim(claimName);
        if (claim instanceof Collection<?> values) {
            values.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        } else if (claim instanceof String value && !value.isBlank()) {
            authorities.add(new SimpleGrantedAuthority(value));
        }
    }
}
