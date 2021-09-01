package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.Notification;
import application.models.NotificationType;
import application.models.Person;
import application.models.dto.CommentAuthorDto;
import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final DaoNotification daoNotification;
    private final DaoPerson daoPerson;

    public GeneralListResponse<NotificationDto> getNotifications() {
        return new GeneralListResponse<>(getNotificationsDtoForNotifications
                (daoNotification.getUserNotifications(daoPerson.getAuthPerson().getId())));
    }

    public GeneralResponse<MessageResponseDto> readNotifications() throws InterruptedException {
        Thread.sleep(5000);
        daoNotification.readNotifications(daoPerson.getAuthPerson().getId());
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }

    private List<NotificationDto> getNotificationsDtoForNotifications(List<Notification> list) {
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for (Notification notification : list) {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setId(notification.getId());
            notificationDto.setNotificationType(daoNotification.getNotificationType(notification.getId()));
            Person person = daoPerson.getById(notification.getSrcPersonId());
            notificationDto.setEntityAuthor(new CommentAuthorDto(person.getId(), person.getFirstName(),
                    person.getLastName(), person.getPhoto()));
            notificationDto.setSentTime(notification.getSentTime());
            notificationDto.setInfo(daoNotification.getNotificationName(notification.getId()));
            notificationDtoList.add(notificationDto);
        }
        return notificationDtoList;
    }
}
