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

        NotificationDTO notificationDTO2 = new NotificationDTO();
        notificationDTO.setId(2);
        notificationDTO.setType_id(2);
        notificationDTO.setSent_time(System.currentTimeMillis());
        notificationDTO.setEntity_id(0);
        notificationDTO.setInfo("Проверка сообщений");

        NotificationDTO notificationDTO3 = new NotificationDTO();
        notificationDTO.setId(3);
        notificationDTO.setType_id(5);
        notificationDTO.setSent_time(System.currentTimeMillis());
        notificationDTO.setEntity_id(1);
        notificationDTO.setInfo("Проверка сообщений");

        NotificationDTO notificationDTO4 = new NotificationDTO();
        notificationDTO.setId(4);
        notificationDTO.setType_id(0);
        notificationDTO.setSent_time(System.currentTimeMillis());
        notificationDTO.setEntity_id(4);
        notificationDTO.setInfo("Проверка сообщений");

        notificationDTOList.add(notificationDTO);
        notificationDTOList.add(notificationDTO2);
        notificationDTOList.add(notificationDTO3);
        notificationDTOList.add(notificationDTO4);

        return new GeneralListResponse<>(notificationDTOList);
    }
}
