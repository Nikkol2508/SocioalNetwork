package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.models.SetPasswordDto;
import application.requests.RecoverPassDtoRequest;
import application.requests.RegistrationDtoRequest;
import application.requests.SetPasswordDtoRequest;
import application.responses.GeneralResponse;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping("/register")
  public ResponseEntity<GeneralResponse<AccountDto>> register(
      @RequestBody RegistrationDtoRequest request)
      throws EmailAlreadyExistsException, PasswordsNotEqualsException {

    return accountService.register(request);
  }

  @PutMapping("/password/set")
  public ResponseEntity<GeneralResponse<SetPasswordDto>> setPassword(
      @RequestBody SetPasswordDtoRequest request) throws Exception {

    return accountService.setPassword(request);
  }

  @PutMapping("/password/recovery")
  public ResponseEntity<GeneralResponse<AccountDto>> recoverPassword(
      @RequestBody RecoverPassDtoRequest request) {
    return ResponseEntity.ok(null);
  }
}
