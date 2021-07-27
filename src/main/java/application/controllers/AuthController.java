package application.controllers;

import application.exceptions.PasswordsNotEqualsException;
import application.models.LogoutDto;
import application.models.PersonDto;
import application.requests.AuthDtoRequest;
import application.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<GeneralResponse<PersonDto>> login(@RequestBody AuthDtoRequest request) throws PasswordsNotEqualsException {
        return ResponseEntity.ok(authService.getAuth(request));
    }

    @PostMapping("/logout")
    private ResponseEntity<GeneralResponse<LogoutDto>> logout() {
        return ResponseEntity.ok(authService.getLogout());
    }
}
