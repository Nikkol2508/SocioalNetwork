package application.service;

import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {


    public GeneralListResponse<NotificationDto> getNotifications() {

        List<NotificationDto> notificationDTOList = new ArrayList<>();

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1);
        notificationDto.setNotificationType("POST_COMMENT");
        notificationDto.setSentTime(System.currentTimeMillis());

        notificationDto.setInfo("Проверка сообщений");

        notificationDTOList.add(notificationDto);

        return new GeneralListResponse<>(notificationDTOList);
    }
}
