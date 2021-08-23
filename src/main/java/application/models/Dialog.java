package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Dialog {

    private int id;

    @JsonProperty("first_user_id")
    private int firstUserId;

    @JsonProperty("second_user_id")
    private int secondUserId;
}
