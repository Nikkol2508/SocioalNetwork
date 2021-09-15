package application.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Integer id;

    @JsonProperty("event_type")
    private String notificationType;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("sent_time")
    private long sentTime;

    @JsonProperty("entity_author")
    private CommentAuthorDto entityAuthor;

    private String info;
}

