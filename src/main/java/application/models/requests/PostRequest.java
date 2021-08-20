package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRequest {

    private String title;

    @JsonProperty("post_text")
    private String postText;

}
