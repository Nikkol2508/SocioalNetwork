package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class PostRequest {

    private String title;

    @JsonProperty("post_text")
    private String postText;

    private List<@NotBlank(message = "{tag.blank}")
    @Pattern(regexp = "^[A-Za-zА-ЯЁа-яё 0-9]+$", message = "{tag.not.valid}") String> tags;

}
