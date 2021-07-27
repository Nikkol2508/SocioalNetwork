package application.controllers;

import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.models.Person;
import application.requests.RegistrationDtoRequest;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final DaoPerson daoPerson;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegistrationDtoRequest registration)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {
        if (!registration.getPasswd1().equals(registration.getPasswd2())) {
            throw new PasswordsNotEqualsException();
        }
        if (daoPerson.getByEmail(registration.getEmail()) != null) {
            throw new EmailAlreadyExistsException();
        }
        Person person = new Person();
        person.setPassword(registration.getPasswd1());
        person.setEmail(registration.getEmail());
        person.setFirstName(registration.getFirstName());
        person.setLastName(registration.getLastName());
        daoPerson.save(person);
        GeneralResponse<AccountDto> response = new GeneralResponse<>();
        response.setTimestamp(System.currentTimeMillis());
        response.setError("");
        response.setData(new AccountDto("ok"));
        return ResponseEntity.ok(response);
    }
}
