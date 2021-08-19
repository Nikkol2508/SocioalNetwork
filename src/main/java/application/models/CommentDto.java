package application.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto {
    private int id;
    private long time;

    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
