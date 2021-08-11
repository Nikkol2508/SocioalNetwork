package application.service;

import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.exceptions.SetPasswordException;
import application.models.AccountDto;
import application.models.Person;
import application.models.SetPasswordDto;
import application.requests.RegistrationDtoRequest;
import application.requests.SetPasswordDtoRequest;
import application.responses.GeneralResponse;
import application.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    daoPerson.save(person);
    log.info("IN register - user: {} successfully registered", person.getEmail());
    GeneralResponse<AccountDto> response = new GeneralResponse<>(new AccountDto("ok"));
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<GeneralResponse<SetPasswordDto>> setPassword(SetPasswordDtoRequest request)
      throws PasswordNotValidException, SetPasswordException {

    /**
     * проверка валидности пароля (не короче 8 символов)
     */
    if (request.getPassword().length() < 8) {
      throw new PasswordNotValidException();
    } else {

      if (daoPerson.setPassword(jwtTokenProvider.getUsername(request.getToken()),
          passwordEncoder.encode(request.getPassword()))) {
        GeneralResponse<SetPasswordDto> response = new GeneralResponse<>(
            new SetPasswordDto("Password changed"));
        return ResponseEntity.ok(response);
      } else {
        GeneralResponse<SetPasswordDto> response = new GeneralResponse<>(
            new SetPasswordDto("Bad request"));
        return ResponseEntity.badRequest().body(response);
      }
    }
  }
}
