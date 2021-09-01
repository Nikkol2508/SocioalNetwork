package application.dao;

import application.dao.mappers.PostCommentMapper;
import application.models.Comment;
import application.models.NotificationType;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoComment implements Dao<Comment> {

    private final JdbcTemplate jdbcTemplate;
    private final DaoNotification daoNotification;
    private final DaoPost daoPost;
    private final DaoPerson daoPerson;

    public List<Comment> getCommentsByPostId(Integer postId) {
        return jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = ? AND parent_id isnull ",
                new Object[]{postId}, new PostCommentMapper());
    }

    public int getPostIdByCommentId(Integer commentId) {
        return jdbcTemplate.queryForObject("SELECT post_id FROM post_comment WHERE id = ?", new Object[]{commentId},
                Integer.class);
    }

    public List<Comment> getSubComment(Integer id) {
        return jdbcTemplate.query("SELECT * FROM post_comment where parent_id = ?", new Object[]{id}, new PostCommentMapper());
    }

    @Override
    public Comment getById(int id) {
        return jdbcTemplate.query("SELECT * FROM post_comment WHERE id = ?", new Object[]{id},
                        new PostCommentMapper()).stream().findAny()
                        .orElse(null);
    }

    @Override
    public List<Comment> getAll() {
        return null;
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
        Person person = daoPerson.getById(daoPost.getById(comment.getPostId()).getAuthorId());
        daoNotification.addNotification(person.getId(), comment.getTime(), comment.getId(), person.getEmail(),
                comment.getParentId() == null ? NotificationType.POST_COMMENT.toString()
                        : NotificationType.COMMENT_COMMENT.toString(), comment.getCommentText());
    }

    @Override
    public void update(Comment comment) {
        jdbcTemplate.update("UPDATE post_comment SET time = ?, post_id = ?, parent_id = ?, author_id = ?, " +
                "comment_text = ?, is_blocked = ? WHERE id = ?",
                comment.getTime(),
                comment.getPostId(),
                comment.getParentId(),
                comment.getAuthorId(),
                comment.getCommentText(),
                comment.isBlocked(),
                comment.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM post_comment where id = ?", id);
    }

    public void deleteByAuthorId(int id){
        jdbcTemplate.update("DELETE FROM post_comment WHERE author_id = ?", id);
    }
}
