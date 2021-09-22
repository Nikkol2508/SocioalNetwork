package application.models.requests;

import application.models.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationRequest {

    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
