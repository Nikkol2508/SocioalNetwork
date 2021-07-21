package application.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {
    private int id;
    private int postId;
    private int parentId;
    private int authorId;
    private String commentText;
    private Date time;
    private boolean isBlocked;
}
