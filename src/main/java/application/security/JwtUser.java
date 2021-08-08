package application.security;

import application.models.Person;
import application.models.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@RequiredArgsConstructor
@Slf4j
public class JwtUser implements UserDetails {

    private final String email;
    private final String password;
    private final Role role;
    private final Set<SimpleGrantedAuthority> authorities;
    private final boolean isBlocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("IN getAuthorities - getting authorities: {}", authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isBlocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isBlocked;
    }

    @Override
    public boolean isEnabled() {
        return !isBlocked;
    }

    public static JwtUser fromPerson(Person person) {
        return new JwtUser(
                person.getEmail(),
                person.getPassword(),
                Role.MODERATOR,
                Role.MODERATOR.getAuthorities(),
                person.isBlocked()
        );
    }
}
