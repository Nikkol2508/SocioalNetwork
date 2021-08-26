package application.models.dto;

import application.models.ReadStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageDto {
    private int id;
    private long time;

    @JsonProperty("author")
    private PersonDialogsDto authorId;

    @JsonProperty("recipient")
    private PersonDialogsDto recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private ReadStatus readStatus;

    @JsonProperty("isSentByMe")
    private boolean isSentByMe;
}
