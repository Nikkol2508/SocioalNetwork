package application.security;

import application.models.Person;
import application.models.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@RequiredArgsConstructor
public class JwtUser implements UserDetails {

    private final String email;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isBlocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
                new ArrayList<>(Role.USER.getAuthorities()),
                person.isBlocked()
        );
    }
}
