package application.controllers;

import application.responses.LanguageResponse;
import application.responses.PersonResponse;
import application.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PersonController {
    private final PersonResponse personResponse;
    private final PersonService personService;

    @GetMapping("api/v1/users/me")
    private ResponseEntity<PersonResponse> getPerson() {
        return new ResponseEntity<>(personService.getPerson(), HttpStatus.OK);
    }
}
