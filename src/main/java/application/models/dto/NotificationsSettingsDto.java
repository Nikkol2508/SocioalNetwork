package application.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationsSettingsDto {
    @JsonProperty("type")
    private String notificationType;
    private Boolean enable;
}
