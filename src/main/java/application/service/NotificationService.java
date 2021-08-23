package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.Notification;
import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
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
        List<Notification> notifications = daoNotification.getUserNotifications(daoPerson.getAuthPerson().getId());
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDtos.add(new NotificationDto(notification.getId(), "FRIEND_REQUEST", notification.getSentTime(),
                    notification.getEntityId(), "dsfsdfs"));
        }
        GeneralListResponse<NotificationDto> listResponse = new GeneralListResponse<>(notificationDtos);
        listResponse.setTotal(0);
        listResponse.setOffset(0);
        listResponse.setPerPage(20);
        return listResponse;
    }
}
