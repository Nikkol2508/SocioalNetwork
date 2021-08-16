package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("comment_text")
    private String commentText;

    private int id;
    private long time;

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
