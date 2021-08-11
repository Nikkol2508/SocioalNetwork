package application.controllers;

import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.AccountDto;
import application.models.Person;
import application.models.SetPasswordDto;
import application.models.requests.RecoverPassDtoRequest;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.responses.GeneralResponse;
import application.models.PasswordRecoveryDto;
import application.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

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
    public ResponseEntity<GeneralResponse<AccountDto>> register(@RequestBody RegistrationDtoRequest request)
            throws EmailAlreadyExistsException, PasswordsNotEqualsException {

        return accountService.register(request);
    }

    @PutMapping("/password/set")
    public ResponseEntity<GeneralResponse<SetPasswordDto>> setPassword(HttpServletRequest servletRequest,
                                                                       @RequestBody SetPasswordDtoRequest request)
            throws PasswordNotValidException {
        String url = servletRequest.getHeader("Referer");
        String code = url.substring(url.indexOf("=") + 1);
        request.setToken(code);
        return accountService.setPassword(request);
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<GeneralResponse<PasswordRecoveryDto>> recoverPassword(
            HttpServletRequest servletRequest, @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {
        String email = request.getEmail();
        String code = RandomString.make(30);


        accountService.updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String resetPasswordLink = siteURL + "/change-password?code=" + code;
        sendEmail(email, resetPasswordLink);
        GeneralResponse<PasswordRecoveryDto> response = new GeneralResponse<>(new PasswordRecoveryDto("ok"));
        return ResponseEntity.ok(response);
    }

    private void sendEmail(String recipientEmail, String link)
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
}
