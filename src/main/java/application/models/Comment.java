package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Comment {
    private int id;

    @JsonProperty("post_id")
    private int postId;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("comment_text")
    private String commentText;

    private long time;

    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
