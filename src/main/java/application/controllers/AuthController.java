package application.controllers;

import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.requests.AuthDtoRequest;
import application.models.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<PersonDto>> login(@RequestBody AuthDtoRequest request)
            throws UsernameNotFoundException, BadCredentialsException {

        log.info("login: start():");
        log.debug("login: request = {}", request);
        GeneralResponse<PersonDto> response = new GeneralResponse<>(authService.login(request));
        log.debug("login: response = {}", response);
        log.info("login: finish():");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> logout() {

        log.info("logout: start():");
        GeneralResponse<MessageResponseDto> response = new GeneralResponse<>(authService.getLogout());
        log.debug("logout: response = {}", response);
        log.info("logout: finish():");

        return ResponseEntity.ok(response);
    }
}
