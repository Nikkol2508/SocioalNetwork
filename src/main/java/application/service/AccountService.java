package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordNotValidException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.NotificationSettingType;
import application.models.PermissionMessagesType;
import application.models.Person;
import application.models.dto.MessageRequestDto;
import application.models.requests.RecoverPassDtoRequest;
import application.models.dto.NotificationsSettingsDto;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.requests.ShiftEmailDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final DaoPerson daoPerson;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final DaoNotification daoNotification;

    public ResponseEntity<GeneralResponse<MessageRequestDto>> register(RegistrationDtoRequest request)
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
        person.setPhoto("storage/stock.jpg");
        person.setMessagesPermission(PermissionMessagesType.ALL.toString());
        person.setApproved(false);
        daoPerson.save(person);
        GeneralResponse<MessageRequestDto> response = new GeneralResponse<>(new MessageRequestDto("ok"));
        setStartNotificationSettings(request.getEmail());
        return ResponseEntity.ok(response);
    }

    public void setStartNotificationSettings (String email) {
        List<NotificationSettingType> codes = Stream.of(NotificationSettingType.values()).collect(Collectors.toList());
        for (int i = 0; i <= codes.size() - 1; i++) {
            daoNotification.setDefaultSettings(daoPerson.getByEmail(email).getId(), codes.get(i).toString());
        }
    }

    public ResponseEntity<GeneralResponse<MessageRequestDto>> setPassword(SetPasswordDtoRequest request)
            throws PasswordNotValidException {
        //проверка валидности пароля (не короче 8 символов)
        if (request.getPassword().length() < 8) {
            throw new PasswordNotValidException();
        } else {
            Person person = getByConfirmationCode(request.getToken());
            if (person == null) {
                throw new EntityNotFoundException("Person with this confirmation code is not found.");
            }
            updatePassword(person, request.getPassword());
            return ResponseEntity.ok(new GeneralResponse<>(new MessageRequestDto("ok")));
        }
    }

    public ResponseEntity<GeneralResponse<MessageRequestDto>> setEmail(ShiftEmailDtoRequest request, String code) {
        // Здесь можно добавить проверку на валидность email (request.getEmail())
        Person person = getByConfirmationCode(code);
        if (person == null) {
            throw new EntityNotFoundException("Person with this confirmation code is not found.");
        }
        updateEmail(person, request.getEmail());
        return ResponseEntity.ok(new GeneralResponse<>(new MessageRequestDto("ok")));
    }

    public void updateConfirmationCode(String code, String email) {
        Person person = daoPerson.getByEmail(email);
        if (person != null) {
            daoPerson.updateConfirmationCode(person.getId(), code);
        } else {
            throw new EntityNotFoundException("Person with email: " + email + " cannot be found");
        }
    }

    public void sendEmailToChangeEmail(String recipientEmail, String link)
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

    public void sendEmailToRecoverPassword(String recipientEmail, String link)
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

    public ResponseEntity<GeneralResponse<MessageRequestDto>> recoverPassword(
            HttpServletRequest servletRequest, @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {
        String email = request.getEmail();
        String code = RandomString.make(30);
        updateConfirmationCode(code, email);
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

    public ResponseEntity<GeneralResponse<MessageRequestDto>> changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = RandomString.make(30);
        updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String resetPasswordLink = siteURL + "/shift-email?code=" + code;
        sendEmailToChangeEmail(email, resetPasswordLink);
        SecurityContextHolder.getContext().setAuthentication(null);
        GeneralResponse<MessageRequestDto> response = new GeneralResponse<>(new MessageRequestDto("ok"));
        return ResponseEntity.ok(response);
    }

    public static String getCode(HttpServletRequest request) {
        String url = request.getHeader("Referer");
        return url.substring(url.indexOf("=") + 1);
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

    private void updateEmail(Person person, String email) {
        daoPerson.updateEmail(person.getId(), email);
        daoPerson.updateConfirmationCode(person.getId(), null);
    }

    public GeneralListResponse<NotificationsSettingsDto> getPersonNotificationsSettings() {
        return new GeneralListResponse<NotificationsSettingsDto>
                (daoNotification.getNotificationsSettings(daoPerson.getAuthPerson().getId()));
    }
}
