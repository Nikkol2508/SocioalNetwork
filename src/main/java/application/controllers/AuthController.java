package application.controllers;

import application.models.Person;
import application.responses.GeneralResponse;
import application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    private ResponseEntity<GeneralResponse<Person>> getLanguages() {
        return ResponseEntity.ok(authService.getAuth());
    }
}
