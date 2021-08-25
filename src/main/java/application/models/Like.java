package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Like {

    private int id;
    private long time;

    @JsonProperty("person_id")
    private int personId;

    @JsonProperty("item_id")
    private int itemId;

    private String type;
}
