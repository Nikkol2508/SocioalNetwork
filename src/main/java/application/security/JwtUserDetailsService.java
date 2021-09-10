package application.security;

import application.dao.DaoPerson;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final DaoPerson daoPerson;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        Person person = daoPerson.getByEmail(email);
        if (person == null) {
            throw new UsernameNotFoundException("User with email: " +  email + " doesn't exists");
        }
        return JwtUser.fromPerson(person);
    }
}
