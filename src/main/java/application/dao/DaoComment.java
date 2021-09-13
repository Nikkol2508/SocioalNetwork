package application.dao;

import application.dao.mappers.PostCommentMapper;
import application.models.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DaoComment {

    private final JdbcTemplate jdbcTemplate;

    public List<Comment> getCommentsByPostId(int postId) {

        return jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = ? AND parent_id isnull",
                new Object[]{postId}, new PostCommentMapper());
    }

    public Integer getPostIdByCommentId(Integer commentId) {

        return jdbcTemplate.queryForObject("SELECT post_id FROM post_comment WHERE id = ?", new Object[]{commentId},
                Integer.class);
    }

    public List<Comment> getSubComment(Integer id) {

        return jdbcTemplate.query("SELECT * FROM post_comment where parent_id = ?", new Object[]{id},
                new PostCommentMapper());
    }

    public Comment getById(int id) {

        return jdbcTemplate.query("SELECT * FROM post_comment WHERE id = ?", new Object[]{id},
                        new PostCommentMapper()).stream().findAny()
                        .orElse(null);
    }

    public Integer save(Comment comment) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("post_comment")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();
        param.put("time", comment.getTime());
        param.put("post_id", comment.getPostId());
        param.put("parent_id", comment.getParentId());
        param.put("author_id", comment.getAuthorId());
        param.put("comment_text", comment.getCommentText());
        param.put("is_blocked", comment.isBlocked());

        return sji.executeAndReturnKey(param).intValue();
    }

    public void update(Comment comment) {

        jdbcTemplate.update("UPDATE post_comment SET time = ?, post_id = ?, parent_id = ?, author_id = ?, " +
                "comment_text = ?, is_blocked = ? WHERE id = ?", comment.getTime(), comment.getPostId(),
                comment.getParentId(), comment.getAuthorId(), comment.getCommentText(), comment.isBlocked(),
                comment.getId());
    }

    public void delete(int id) {

        jdbcTemplate.update("DELETE FROM post_comment where id = ?", id);
    }

    public void deleteByAuthorId(int id) {

        jdbcTemplate.update("DELETE FROM post_comment WHERE author_id = ?", id);
    }
}
