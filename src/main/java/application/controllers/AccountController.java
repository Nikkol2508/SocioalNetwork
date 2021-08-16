package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.dto.MessageRequestDto;
import application.models.requests.RecoverPassDtoRequest;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.requests.ShiftEmailDtoRequest;
import application.models.responses.GeneralResponse;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final JavaMailSender mailSender;
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> register(@RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {

        return accountService.register(request);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> setPassword(HttpServletRequest servletRequest,
                                                                          @RequestBody SetPasswordDtoRequest request)
            throws PasswordNotValidException {
        request.setToken(getCode(servletRequest));
        return accountService.setPassword(request);
    }

    @PutMapping("/email")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> setEmail(HttpServletRequest servletRequest,
                                                                 @RequestBody ShiftEmailDtoRequest request) {
        return accountService.setEmail(request, getCode(servletRequest));
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> recoverPassword(
            HttpServletRequest servletRequest, @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {
        String email = request.getEmail();
        String code = RandomString.make(30);
        accountService.updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String urn = servletRequest.getHeader("Referer").indexOf("settings") > 0
                ? "/shift-password"
                : "/change-password";
        String resetPasswordLink = siteURL + urn + "?code=" + code;
        sendEmailToRecoverPassword(email, resetPasswordLink);
        GeneralResponse<MessageRequestDto> response = new GeneralResponse<>(new MessageRequestDto("ok"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/shift-email")
    public ResponseEntity<GeneralResponse<MessageRequestDto>> changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = RandomString.make(30);
        accountService.updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String resetPasswordLink = siteURL + "/shift-email?code=" + code;
        sendEmailToChangeEmail(email, resetPasswordLink);
        SecurityContextHolder.getContext().setAuthentication(null);
        GeneralResponse<MessageRequestDto> response = new GeneralResponse<>(new MessageRequestDto("ok"));
        return ResponseEntity.ok(response);
    }

    private void sendEmailToChangeEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("social.network.skillbox@yandex.ru", "Support");
        helper.setTo(recipientEmail);
        String subject = "Here's the link to confirm your new email";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to change your email.</p>"
                + "<p>Click the link below to change your email:</p>"
                + "<p><a href=\"" + link + "\">Change my email</a></p>"
                + "<br>"
                + "<p>Ignore this email if you don't want to change email, "
                + "or you have not made the request.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    private void sendEmailToRecoverPassword(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("social.network.skillbox@yandex.ru", "Support");
        helper.setTo(recipientEmail);
        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    private String getCode(HttpServletRequest request) {
        String url = request.getHeader("Referer");
        return url.substring(url.indexOf("=") + 1);
    }
}
