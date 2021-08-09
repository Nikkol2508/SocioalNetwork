package application.dao;

import application.models.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoComment {

    private final JdbcTemplate jdbcTemplate;

    public List<Comment> getCommentsByPostId(Integer post_id) {
        return jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = ?", new Object[]{post_id}, new PostCommentMapper());
    }

    public void save(Comment comment) {
        jdbcTemplate.update("INSERT INTO post_comment (time, post_id, parent_id, author_id, comment_text, is_blocked) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
                comment.getTime(),
                comment.getPostId(),
                comment.getParentId(),
                comment.getAuthorId(),
                comment.getCommentText(),
                comment.isBlocked());
    }

}
