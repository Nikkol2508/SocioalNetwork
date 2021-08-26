package application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {
    private int id;
    private long time;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("recipient_id")
    private int recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private ReadStatus readStatus;

    @JsonIgnore
    private int dialogId;
}
