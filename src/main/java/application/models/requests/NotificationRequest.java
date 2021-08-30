package application.models.requests;

import lombok.Data;

@Data
public class NotificationRequest {
    private String notification_type;
    private boolean enable;
}
