package application.models.requests;

import lombok.Data;

@Data
public class PostRequest {

    private String title;
    private String post_text;

}
