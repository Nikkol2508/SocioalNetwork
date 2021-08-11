package application.models;

import lombok.Data;

@Data
public class Post {
    private int id;
    private long time;
    private int authorId;
    private String title;
    private String postText;
    private boolean isBlocked;
}

