package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.requests.RecoverPassDtoRequest;
import application.requests.RegistrationDtoRequest;
import application.requests.SetPasswordDtoRequest;
import application.responses.GeneralResponse;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<AccountDto>> register(@RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {
        return accountService.savePerson(request);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<AccountDto>> setPassword(@RequestBody SetPasswordDtoRequest request) {
        return ResponseEntity.ok(null);
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<AccountDto>> recoverPassword(@RequestBody RecoverPassDtoRequest request) {
        return ResponseEntity.ok(null);
    }
}
