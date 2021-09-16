package application.models.dto;

import application.models.Post;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {
    private int id;
    private long time;
    private PersonDto author;
    private String title;

    @JsonProperty("likes")
    private int countLikes;

    @JsonProperty("my_like")
    private int myLike;

    private List<CommentDto> comments;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    private List<String> tags;

    public static PostDto fromPost(Post post, int likes, PersonDto author, List<CommentDto> comments, List<String> tags, int myLike) {

        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setPostText(post.getPostText());
        postDto.setTitle(post.getTitle());
        postDto.setBlocked(post.isBlocked());
        postDto.setTime(post.getTime());
        postDto.setCountLikes(likes);
        postDto.setAuthor(author);
        postDto.setComments(comments);
        postDto.setTags(tags);
        postDto.setMyLike(myLike);

        return postDto;
    }
}

