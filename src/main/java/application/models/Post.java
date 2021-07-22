package application.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Post {
    private int id;
    private Date time;
    private int authorId;
    private String title;
    private String postText;
    private boolean isBlocked;
}

