package application.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {

    private Integer id;

    @JsonProperty("notification_type")
    private String notificationType;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("sent_time")
    private long sentTime;

    @JsonProperty("entity_id")
    private int entityId;

    private String info;
}
