package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonPropertyOrder({"parent_id", "comment_text", "id", "postId", "time", "authorId", "isBlocked"})
public class Comment {

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("comment_text")
    private String commentText;

    private int id;

    @JsonProperty("post_id")
    private Integer postId;

    private long time;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
