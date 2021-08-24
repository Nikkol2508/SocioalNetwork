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
import application.models.dto.NotificationsSettingsDto;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.requests.ShiftEmailDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final DaoPerson daoPerson;
    private final BCryptPasswordEncoder passwordEncoder;
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
