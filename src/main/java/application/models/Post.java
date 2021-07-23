package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Post {
    private int id;
    private long time;
    private Person author;
    private String title;
    private int likes;
    private List<Comment> comments;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;


}

