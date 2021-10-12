package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.Notification;
import application.models.Person;
import application.models.dto.CommentAuthorDto;
import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final DaoNotification daoNotification;
    private final DaoPerson daoPerson;

    public List<NotificationDto> getNotifications() {

        return cleanNotifications(getNotificationsDtoForNotifications(daoNotification.getUserNotifications(
                daoPerson.getAuthPerson().getId())));
    }

    private List<NotificationDto> cleanNotifications (List<NotificationDto> list) {

        List<String> userBlockNotifications = daoNotification.getBlockNotification(daoPerson.getAuthPerson().getId());
        list.forEach(notificationDto -> {
            if (userBlockNotifications.contains(notificationDto.getNotificationType())) {
                daoNotification.readNotificationForId(notificationDto.getId());
            }
        });
        return list;
    }

    public MessageResponseDto readNotifications(Boolean all, Integer id) {

        if (id == null) {
            daoNotification.readNotifications(daoPerson.getAuthPerson().getId());
        } else if (all == null) {
            daoNotification.readNotificationForId(id);
        }
        return new MessageResponseDto();
    }

    private List<NotificationDto> getNotificationsDtoForNotifications(List<Notification> list) {

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for (Notification notification : list) {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setId(notification.getId());
            notificationDto.setNotificationType(notification.getType());
            Person person = daoPerson.getById(notification.getSrcPersonId());
            notificationDto.setEntityAuthor(new CommentAuthorDto(person.getId(), person.getFirstName(),
                    person.getLastName(), person.getPhoto()));
            notificationDto.setSentTime(notification.getSentTime());
            notificationDto.setInfo(notification.getName());
            notificationDtoList.add(notificationDto);
        }
        return notificationDtoList;
    }
}
