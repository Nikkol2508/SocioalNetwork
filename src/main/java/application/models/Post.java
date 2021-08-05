package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Post {
    private int id;
    private long time;
    private int authorId;
    private String title;
    private String postText;
    private boolean isBlocked;
}
