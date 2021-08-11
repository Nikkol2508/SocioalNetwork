package application.controllers;

import application.dao.DaoPerson;
import application.models.LogoutDto;
import application.models.Person;
import application.models.PersonDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:8086", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final DaoPerson daoPerson;
    private final PasswordEncoder passwordEncoder;

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
    private ResponseEntity<GeneralResponse<LogoutDto>> logout() {
        return ResponseEntity.ok(authService.getLogout());
    }

//    @PostMapping("/change-password")
//    public String processResetPassword(@RequestParam("code") String code, HttpServletRequest request) {
//        String code = request.getParameter("code");
//        String password = request.getParameter("password");
//        Person person = daoPerson.getByConfirmationCode(code);
//        if (person == null) {
//
//        } else {
//            person.setPassword(passwordEncoder.encode(password));
//            daoPerson.updatePassword(person);
//        }
//        return "index";
//    }
}
