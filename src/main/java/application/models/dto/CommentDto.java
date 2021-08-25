package application.models.dto;

import application.models.Comment;
import application.models.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CommentDto {

    private Integer parentId;
    private String comment_text;
    private int id;
    private String postId;
    private long time;
    private CommentAuthorDto author;
    private boolean isBlocked;
    private List<CommentDto> sub_comments;
    private int likes;

    @JsonProperty("my_like")
    private int myLike;

    public static CommentDto fromComment(Comment comment, Person person, List<CommentDto> subCommentList, int myLike) {
        CommentDto commentDto = new CommentDto();
        commentDto.setParentId(comment.getParentId());
        commentDto.setComment_text(comment.getCommentText());
        commentDto.setId(comment.getId());
        commentDto.setPostId(String.valueOf(comment.getPostId()));
        commentDto.setTime(comment.getTime());

        CommentAuthorDto author = new CommentAuthorDto();
        author.setId(person.getId());
        author.setFirstName(person.getFirstName());
        author.setLastName(person.getLastName());
        commentDto.setAuthor(author);

        commentDto.setBlocked(comment.isBlocked());
        commentDto.setSub_comments(subCommentList);
        commentDto.setLikes(5);
        commentDto.setMyLike(myLike);

        return commentDto;
    }

}
