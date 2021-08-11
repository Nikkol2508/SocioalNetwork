package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Like {

    private int id;
    private long time;

    @JsonProperty("person_id")
    private int personId;

    @JsonProperty("post_id")
    private int postId;
}
