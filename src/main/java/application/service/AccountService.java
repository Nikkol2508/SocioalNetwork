package application.service;

import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.models.Person;
import application.requests.RegistrationDtoRequest;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final DaoPerson daoPerson;

    public ResponseEntity<GeneralResponse<AccountDto>> savePerson(RegistrationDtoRequest request)
            throws PasswordsNotEqualsException, EmailAlreadyExistsException {
        if (!request.getPasswd1().equals(request.getPasswd2())) {
            throw new PasswordsNotEqualsException();
        }
        if (daoPerson.getByEmail(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException();
        }
        Person person = new Person();
        person.setPassword(request.getPasswd1());
        person.setEmail(request.getEmail());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        daoPerson.save(person);
        GeneralResponse<AccountDto> response = new GeneralResponse<>(new AccountDto("ok"));
        return ResponseEntity.ok(response);
    }


}
