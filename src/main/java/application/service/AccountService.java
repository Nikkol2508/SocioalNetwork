package application.service;

import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.models.PermissionMessagesType;
import application.models.Person;
import application.models.SetPasswordDto;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.responses.GeneralResponse;
import application.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final DaoPerson daoPerson;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<GeneralResponse<AccountDto>> register(RegistrationDtoRequest request)
            throws PasswordsNotEqualsException, EmailAlreadyExistsException {
        if (!request.getPasswd1().equals(request.getPasswd2())) {
            throw new PasswordsNotEqualsException();
        }
        if (daoPerson.getByEmail(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException();
        }
        Person person = new Person();
        person.setPassword(passwordEncoder.encode(request.getPasswd1()));
        person.setEmail(request.getEmail());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setMessagesPermission(PermissionMessagesType.ALL);
        person.setApproved(false);
        daoPerson.save(person);
        log.info("IN register - user: {} successfully registered", person.getEmail());
        GeneralResponse<AccountDto> response = new GeneralResponse<>(new AccountDto("ok"));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GeneralResponse<SetPasswordDto>> setPassword(SetPasswordDtoRequest request)
            throws PasswordNotValidException {
        /*
         * проверка валидности пароля (не короче 8 символов)
         */
        if (request.getPassword().length() < 8) {
            throw new PasswordNotValidException();
        } else {
            Person person = getByConfirmationCode(request.getToken());
            if (person == null) {
                throw new EntityNotFoundException("Person with this token is not found.");
            }
            updatePassword(person, request.getPassword());
            return ResponseEntity.ok(new GeneralResponse<>(new SetPasswordDto("ok")));
        }
    }

    public void updateConfirmationCode(String code, String email) {
        Person person = daoPerson.getByEmail(email);
        if (person != null) {
            daoPerson.updateConfirmationCode(person.getId(), code);
        } else {
            throw new EntityNotFoundException("Person with email: " + email + " cannot be found");
        }
    }

    private Person getByConfirmationCode(String code) {
        return daoPerson.getByConfirmationCode(code);
    }

    private void updatePassword(Person person, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        int personId = person.getId();
        daoPerson.updatePassword(personId, encodedPassword);
        daoPerson.updateConfirmationCode(personId, null);
    }

}
