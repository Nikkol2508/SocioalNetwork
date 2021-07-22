package application.сontrollers;

import application.responses.LanguageResponse;
import application.responses.PersonResponse;
import application.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final PersonResponse personResponse;
    private final PersonService personService;

    @PostMapping("/auth/login")
    private ResponseEntity<PersonResponse> getLanguages() {
        return ResponseEntity.ok(personService.getPerson());
    }
}
