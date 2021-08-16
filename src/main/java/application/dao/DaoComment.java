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

    public List<Comment> getCommentsByPostId(Integer postId) {
        return jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = ? AND parent_id isnull ", new Object[]{postId}, new PostCommentMapper());
    }

    public int getPostIdByCommentId(Integer commentId) {
        return jdbcTemplate.queryForObject("SELECT post_id FROM post_comment WHERE id = ?", new Object[]{commentId}, Integer.class);
    }

    public List<Comment> getSubComment(Integer id) {
        return jdbcTemplate.query("SELECT * FROM post_comment where parent_id = ?", new Object[]{id}, new PostCommentMapper());
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
