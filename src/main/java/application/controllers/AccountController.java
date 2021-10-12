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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> register(
            @Valid @RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {

        log.info("register: start():");
        log.debug("register: request = {}", request);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(accountService.register(request));
        log.debug("register: response = {}", generalResponse);
        log.info("register: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setPassword(
            HttpServletRequest servletRequest,
            @Valid @RequestBody SetPasswordDtoRequest request) {

        request.setToken(AccountService.getCode(servletRequest));

        log.info("setPassword: start():");
        log.debug("setPassword: request = {}", request);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(accountService.setPassword(request));
        log.debug("setPassword: response = {}", generalResponse);
        log.info("setPassword: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setEmail(
            HttpServletRequest servletRequest, @Valid @RequestBody ShiftEmailDtoRequest request) {

        log.info("setEmail: start():");
        log.debug("setEmail: request = {}", request);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(accountService.setEmail(request,
                AccountService.getCode(servletRequest)));
        log.debug("setEmail: response = {}", generalResponse);
        log.info("setEmail: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> recoverPassword(
            HttpServletRequest servletRequest, @Valid @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {

        log.info("recoverPassword: start():");
        log.debug("recoverPassword: request = {}", request);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>
                (accountService.recoverPassword(servletRequest, request));
        log.debug("recoverPassword: response = {}", generalResponse);
        log.info("recoverPassword: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/shift-email")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {

        log.info("changeEmail: start():");
        log.debug("changeEmail: request = {}", servletRequest);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>
                (accountService.changeEmail(servletRequest));
        log.debug("changeEmail: response = {}", generalResponse);
        log.info("changeEmail: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("/notifications")
    public ResponseEntity<GeneralListResponse<NotificationsSettingsDto>> getAccountNotificationsSettings(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getPersonNotificationsSettings: start():");
        log.debug("getPersonNotificationsSettings: offset = {}, itemPerPage = {}", offset, itemPerPage);
        GeneralListResponse<NotificationsSettingsDto> generalResponse = new GeneralListResponse<>(
                accountService.getPersonNotificationsSettings(), offset, itemPerPage);
        log.debug("getPersonNotificationsSettings: response = {}", generalResponse);
        log.info("getPersonNotificationsSettings: finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/notifications")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setAccountNotificationsSettings(
            @RequestBody NotificationRequest notificationRequest) {

        log.info("setNotificationSettings: start():");
        log.debug("setNotificationSettings: notificationRequest = {}", notificationRequest);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>
                (accountService.setNotificationSettings(notificationRequest));
        log.debug("setNotificationSettings: response = {}", generalResponse);
        log.info("setNotificationSettings: finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
