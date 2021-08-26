package application.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DialogDto {

    private int id;

    @JsonProperty("unread_count")
    private int unreadCount;

    private PersonDialogsDto recipient;

    @JsonProperty("last_message")
    private MessageDto lastMessage;
}
