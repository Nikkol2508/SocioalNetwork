package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.exceptions.EmailAlreadyExistsException;
import application.exceptions.PasswordsNotEqualsException;
import application.models.PermissionMessagesType;
import application.models.Person;
import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationsSettingsDto;
import application.models.requests.*;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final DaoPerson daoPerson;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DaoNotification daoNotification;
    private final JavaMailSender mailSender;

    public MessageResponseDto register(RegistrationDtoRequest request)
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
        person.setPhoto(null);
        person.setMessagesPermission(PermissionMessagesType.ALL.toString());
        person.setApproved(false);
        daoPerson.save(person);
        daoNotification.setDefaultSettings(daoPerson.getByEmail(person.getEmail()).getId());
        return new MessageResponseDto();
    }

    public MessageResponseDto setPassword(SetPasswordDtoRequest request) {

        updatePassword(getPersonByConfirmationCode(request.getToken()), request.getPassword());
        return new MessageResponseDto();
    }

    public MessageResponseDto setEmail(ShiftEmailDtoRequest request, String code) {

        updateEmail(getPersonByConfirmationCode(code), request.getEmail());
        return new MessageResponseDto();
    }

    public MessageResponseDto recoverPassword(
            HttpServletRequest servletRequest, @RequestBody RecoverPassDtoRequest request)
            throws MessagingException, UnsupportedEncodingException {

        String email = request.getEmail();
        String code = RandomString.make(30);
        updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String urn = servletRequest.getHeader("Referer").contains("settings") ? "/shift-password"
                : "/change-password";
        String resetPasswordLink = siteURL + urn + "?code=" + code;
        sendEmailToRecoverPassword(email, resetPasswordLink);
        return new MessageResponseDto();
    }

    public MessageResponseDto changeEmail(HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = RandomString.make(30);
        updateConfirmationCode(code, email);
        String siteURL = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getServletPath(), "");
        String resetPasswordLink = siteURL + "/shift-email?code=" + code;
        sendEmailToChangeEmail(email, resetPasswordLink);
        return new MessageResponseDto();
    }

    public static String getCode(HttpServletRequest request) {

        String url = request.getHeader("Referer");
        return url.substring(url.indexOf("=") + 1);
    }

    public List<NotificationsSettingsDto> getPersonNotificationsSettings() {

        return daoNotification.getNotificationsSettings(daoPerson.getAuthPerson().getId());
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

    private void updateConfirmationCode(String code, String email) {

        Person person = daoPerson.getByEmail(email);
        if (person != null) {
            daoPerson.updateConfirmationCode(person.getId(), code);
        } else {
            throw new EntityNotFoundException("Person with email: " + email + " cannot be found");
        }
    }

    private Person getPersonByConfirmationCode(String code) {

        Person person = daoPerson.getByConfirmationCode(code);
        if (person == null) {
            throw new EntityNotFoundException("This link is no longer active, check your mail to find actual link");
        }
        return person;
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

    public MessageResponseDto setNotificationSettings(NotificationRequest request) {

        daoNotification.setSettings(daoPerson.getAuthPerson().getId(), request.getNotificationType(),
                request.isEnable());
        return new MessageResponseDto();
    }

}
