package application.models;

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

    public static CommentDto fromComment(Comment comment, Person person, List<CommentDto> subCommentList) {
        CommentDto commentDto = new CommentDto();
        commentDto.setParentId(comment.getParentId());
        commentDto.setComment_text(comment.getCommentText());
        commentDto.setId(comment.getId());
        commentDto.setPostId(String.valueOf(comment.getPostId()));
        commentDto.setTime(comment.getTime());

        CommentAuthorDto author = new CommentAuthorDto();
        author.setId(person.getId());
        author.setFirst_name(person.getFirstName());
        author.setLast_name(person.getLastName());
        commentDto.setAuthor(author);

        commentDto.setBlocked(comment.isBlocked());

        commentDto.setSub_comments(subCommentList);

        return commentDto;
    }

}
