package application.controllers;

import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.requests.AuthDtoRequest;
import application.models.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @PostMapping("/login")
    private ResponseEntity<GeneralResponse<PersonDto>> login(@RequestBody AuthDtoRequest request)
            throws UsernameNotFoundException, BadCredentialsException {
        return authService.login(request);
    }

    @PostMapping("/logout")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> logout() {
        return ResponseEntity.ok(authService.getLogout());
    }
}
