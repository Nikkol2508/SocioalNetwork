package application.models.requests;

import lombok.Data;

@Data
public class CommentRequest {

    private Integer parent_id;
    private String comment_text;
}
