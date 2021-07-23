package application.controllers;

import application.models.LogoutDto;
import application.models.Person;
import application.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    private ResponseEntity<GeneralResponse<Person>> login() {
        return ResponseEntity.ok(authService.getAuth());
    }

    @PostMapping("/logout")
    private ResponseEntity<GeneralResponse<LogoutDto>> logout() {
        return ResponseEntity.ok(authService.getLogout());
    }
}
