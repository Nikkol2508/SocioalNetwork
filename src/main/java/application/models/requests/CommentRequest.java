package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("comment_text")
    @NotBlank(message = "{comment.text.blank}")
    private String commentText;
}
