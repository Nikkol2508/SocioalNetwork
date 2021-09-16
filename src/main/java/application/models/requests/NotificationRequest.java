package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationRequest {

    @JsonProperty("notification_type")
    private String notificationType;
    private boolean enable;
}
