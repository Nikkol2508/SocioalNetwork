package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationsSettingsDto;
import application.models.requests.RecoverPassDtoRequest;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.requests.ShiftEmailDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> register(@RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {

        return accountService.register(request);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setPassword(HttpServletRequest servletRequest,
                                                                           @RequestBody SetPasswordDtoRequest request)
            throws PasswordNotValidException {
        request.setToken(AccountService.getCode(servletRequest));
        return accountService.setPassword(request);
    }

    @PutMapping("/email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setEmail(HttpServletRequest servletRequest,
                                                                        @RequestBody ShiftEmailDtoRequest request) {
        return accountService.setEmail(request, AccountService.getCode(servletRequest));
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> recoverPassword(
            HttpServletRequest servletRequest, @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {
        return accountService.recoverPassword(servletRequest, request);
    }

    @PutMapping("/shift-email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {
        return accountService.changeEmail(servletRequest);
    }

    @GetMapping("/notifications")
    public ResponseEntity<GeneralListResponse<NotificationsSettingsDto>> getAccountNotifications() {
        return ResponseEntity.ok(accountService.getPersonNotificationsSettings());
    }
}
