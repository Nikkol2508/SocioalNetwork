package application.security;

import application.dao.DaoPerson;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final DaoPerson daoPerson;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person person = daoPerson.getByEmail(email);
        if (person == null) {
            throw new UsernameNotFoundException("User with email: " +  email + "doesn't exists");
        }
        log.info("IN loadUserByUsername - user with email: {} successfully loaded", person.getEmail());
        JwtUser jwtUser = JwtUser.fromPerson(person);
        log.info("IN loadUserByUsername - created jwtUser: {}", jwtUser);
        return jwtUser;
    }
}
