package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageSendDtoRequest {

    @JsonProperty("message_text")
    @NotBlank(message = "{message.text.blank}")
    private String messageText;
}
