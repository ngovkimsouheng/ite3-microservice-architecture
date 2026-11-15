package kh.edu.istad.iam.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;
import java.util.UUID;

@Getter
public class IamUserDetails extends User {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID uuid;

    public IamUserDetails(UUID uuid, String username, String password, boolean enabled,
                          boolean accountNonExpired, boolean credentialsNonExpired,
                          boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.uuid = uuid;
    }
}

