package application.models.requests;

import application.models.NotificationSettingType;
import application.models.NotificationType;
import lombok.Data;

@Data
public class NotificationRequest {
    private String notification_type;
    private boolean enable;
}
