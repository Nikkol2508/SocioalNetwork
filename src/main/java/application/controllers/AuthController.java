package application.controllers;

import application.models.Person;
import application.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    private ResponseEntity<GeneralResponse<Person>> getLogin() {
        return ResponseEntity.ok(authService.getAuth());
    }

    @PostMapping("/logout")
    private ResponseEntity<GeneralResponse<Person>> getLogout() {
        return ResponseEntity.ok(authService.getLogout());
    }
}
