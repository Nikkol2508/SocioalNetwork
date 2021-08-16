package application.controllers;

import application.dao.DaoPerson;
import application.models.dto.MessageRequestDto;
import application.models.Person;
import application.models.dto.PersonDto;
import application.models.requests.AuthDtoRequest;
import application.models.responses.GeneralResponse;
import application.security.JwtTokenProvider;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final DaoPerson daoPerson;

    @PostMapping("/login")
    private ResponseEntity<GeneralResponse<PersonDto>> login(@RequestBody AuthDtoRequest request)
            throws UsernameNotFoundException, BadCredentialsException {
        try {
            String email = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            Person person = daoPerson.getByEmail(email);
            if (person == null) {
                throw new UsernameNotFoundException("User with email: " + email + " not found");
            }
            String token = jwtTokenProvider.createToken(email);
            return ResponseEntity.ok(authService.getAuth(request, token));
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    private ResponseEntity<GeneralResponse<MessageRequestDto>> logout() {
        return ResponseEntity.ok(authService.getLogout());
    }
}
