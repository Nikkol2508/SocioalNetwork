package application.dao;

import application.dao.mappers.PostCommentMapper;
import application.models.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoComment {

    private final JdbcTemplate jdbcTemplate;

    public List<Comment> getCommentsByPostId(int postId) {

        log.info("getCommentsByPostId(): start():");
        log.debug("getCommentsByPostId(): postId = {}", postId);
        List<Comment> commentList = jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = ? AND parent_id isnull",
                new Object[]{postId}, new PostCommentMapper());
        log.debug("getCommentsByPostId(): commentList = {}", commentList);
        log.info("getCommentsByPostId(): finish():");
        return commentList;
    }

    public Integer getPostIdByCommentId(Integer commentId) {

        log.info("getPostIdByCommentId(): start():");
        log.debug("getPostIdByCommentId(): commentId = {}", commentId);
        Integer postId = jdbcTemplate.queryForObject("SELECT post_id FROM post_comment WHERE id = ?", new Object[]{commentId},
                Integer.class);
        log.debug("getPostIdByCommentId(): postId = {}", postId);
        log.info("getPostIdByCommentId(): finish():");
        return postId;
    }

    public List<Comment> getSubComment(Integer id) {
        log.info("getSubComment(): start():");
        log.debug("getSubComment(): id = {}", id);
        List<Comment> subCommentList = jdbcTemplate.query("SELECT * FROM post_comment where parent_id = ?", new Object[]{id},
                new PostCommentMapper());
        log.debug("getSubComment(): subCommentList = {}", subCommentList);
        log.info("getSubComment(): finish():");
        return subCommentList;
    }

    public Comment getById(int id) {
        log.info("getById(): start():");
        log.debug("getById(): id = {}", id);
        Comment comment = jdbcTemplate.query("SELECT * FROM post_comment WHERE id = ?", new Object[]{id},
                new PostCommentMapper()).stream().findAny()
                .orElse(null);
        log.debug("getById(): comment = {}", comment);
        log.info("getById(): finish():");
        return comment;
    }

    public Integer save(Comment comment) {
        log.info("save(): start():");
        log.debug("save(): comment = {}", comment);
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("post_comment")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> param = new HashMap<>();
        param.put("time", comment.getTime());
        param.put("post_id", comment.getPostId());
        param.put("parent_id", comment.getParentId());
        param.put("author_id", comment.getAuthorId());
        param.put("comment_text", comment.getCommentText());
        param.put("is_blocked", comment.isBlocked());
        Integer key = sji.executeAndReturnKey(param).intValue();
        log.debug("save(): key = {}", key);
        log.info("save(): finish():");
        return key;
    }

    public void update(Comment comment) {
        log.info("update(): start():");
        log.debug("update(): comment = {}", comment);
        jdbcTemplate.update("UPDATE post_comment SET time = ?, post_id = ?, parent_id = ?, author_id = ?, " +
                "comment_text = ?, is_blocked = ? WHERE id = ?", comment.getTime(), comment.getPostId(),
                comment.getParentId(), comment.getAuthorId(), comment.getCommentText(), comment.isBlocked(),
                comment.getId());
        log.info("update(): finish():");
    }

    public void delete(int id) {
        log.info("delete(): start():");
        log.debug("delete(): id = {}", id);
        jdbcTemplate.update("DELETE FROM post_comment where id = ?", id);
        log.info("delete(): finish():");
    }

    public void deleteByAuthorId(int id) {
        log.info("deleteByAuthorId(): start():");
        log.debug("deleteByAuthorId(): id = {}", id);
        jdbcTemplate.update("DELETE FROM post_comment WHERE author_id = ?", id);
        log.info("deleteByAuthorId(): finish():");
    }
}
