package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationsSettingsDto;
import application.models.requests.*;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private static final Logger logger = LogManager.getLogger("app");

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> register(
            @Valid @RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {

        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(accountService.register(request));
        //Реализовать сокрытие данных пользователя
        logger.info("Register(): start(): request = {}, response = {}", " ", generalResponse);
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setPassword(
            HttpServletRequest servletRequest,
            @Valid @RequestBody SetPasswordDtoRequest request) {

        request.setToken(AccountService.getCode(servletRequest));
        return ResponseEntity.ok(new GeneralResponse<>(accountService.setPassword(request)));
    }

    @PutMapping("/email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setEmail(
            HttpServletRequest servletRequest, @Valid @RequestBody ShiftEmailDtoRequest request) {

        return ResponseEntity.ok(new GeneralResponse<>(accountService.setEmail(request,
                AccountService.getCode(servletRequest))));
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> recoverPassword(
            HttpServletRequest servletRequest, @Valid @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {

        return ResponseEntity.ok(new GeneralResponse<>(accountService.recoverPassword(servletRequest, request)));
    }

    @PutMapping("/shift-email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {

        return ResponseEntity.ok(new GeneralResponse<>(accountService.changeEmail(servletRequest)));
    }

    @GetMapping("/notifications")
    public ResponseEntity<GeneralListResponse<NotificationsSettingsDto>> getAccountNotificationsSettings(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(
                accountService.getPersonNotificationsSettings(), offset, itemPerPage));
    }

    @PutMapping("/notifications")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setAccountNotificationsSettings(
            @RequestBody NotificationRequest notificationRequest) {

        return ResponseEntity.ok(new GeneralResponse<>(accountService.setNotificationSettings(notificationRequest)));
    }

}
