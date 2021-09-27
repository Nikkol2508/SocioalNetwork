package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PostRequest {

    @NotBlank(message = "{}")
    private String title;

    @JsonProperty("post_text")
    @NotBlank(message = "{}")
    private String postText;

    private List<String> tags;

}
