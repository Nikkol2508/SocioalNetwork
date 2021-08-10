package application.service;

import application.models.NotificationDTO;
import application.responses.GeneralListResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {


    public GeneralListResponse<NotificationDTO> getNotifications() {

        List<NotificationDTO> notificationDTOList = new ArrayList<>();

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(1);
        notificationDTO.setType_id(1);
        notificationDTO.setSent_time(System.currentTimeMillis());
        notificationDTO.setEntity_id(1);
        notificationDTO.setInfo("Проверка сообщений");

        notificationDTOList.add(notificationDTO);

        return new GeneralListResponse<>(notificationDTOList);
    }
}
