package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.Notification;
import application.models.NotificationType;
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
        List<Notification> notifications = daoNotification.getUserNotifications(daoPerson.getAuthPerson().getId());
        List<NotificationDto> notificationDtos = new ArrayList<>();
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1);
        notificationDto.setNotificationType(NotificationType.FRIEND_REQUEST.toString());
        notificationDto.setSentTime(System.currentTimeMillis());
        notificationDto.setEntityAuthor(new CommentAuthorDto(536, "Егор", "Чаплин"));
        notificationDto.setInfo("asddasdas");
        notificationDtos.add(notificationDto);
        GeneralListResponse<NotificationDto> listResponse = new GeneralListResponse<>(notificationDtos);
        listResponse.setTotal(0);
        listResponse.setOffset(0);
        listResponse.setPerPage(20);
        return listResponse;
    }

    public GeneralResponse<MessageResponseDto> readNotifications() {
        daoNotification.readNotifications(daoPerson.getAuthPerson().getId());
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }
}
