package application.models;

import lombok.Data;

@Data
public class CommentDto {

    private Integer parentId;
    private String commentText;
    private int id;
    private String postId;
    private long time;
    private Person author;
    private boolean isBlocked;
}
