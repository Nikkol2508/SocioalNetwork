package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageSendDtoRequest {

    @JsonProperty("message_text")
    private String messageText;
}
