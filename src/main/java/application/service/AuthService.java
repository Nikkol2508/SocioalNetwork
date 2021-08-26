package application.service;

import application.dao.DaoPerson;
import application.models.Person;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.requests.AuthDtoRequest;
import application.models.responses.GeneralResponse;
import application.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DaoPerson daoPerson;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<GeneralResponse<PersonDto>> login(@RequestBody AuthDtoRequest request)
            throws UsernameNotFoundException, BadCredentialsException {
        try {
            String email = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            Person person = getPerson(email);
            if (person == null) {
                throw new UsernameNotFoundException("User with email: " + email + " not found");
            }
            String token = jwtTokenProvider.createToken(email);
            return ResponseEntity.ok(getAuth(request, token));
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public GeneralResponse<MessageResponseDto> getLogout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }

    private GeneralResponse<PersonDto> getAuth(AuthDtoRequest authDtoRequest, String token) {
        Person person = daoPerson.getByEmail(authDtoRequest.getEmail());
        PersonDto personDto = PersonDto.fromPerson(person);
        personDto.setToken(token);
        return new GeneralResponse<>(personDto);
    }

    private Person getPerson(String email) {
        return daoPerson.getByEmail(email);
    }

}
