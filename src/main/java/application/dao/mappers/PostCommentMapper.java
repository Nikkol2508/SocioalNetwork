package application.dao.mappers;

import application.models.Comment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostCommentMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {

        Comment comment = new Comment();
        comment.setParentId(rs.getInt("parent_id"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setId(rs.getInt("id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setTime(rs.getLong("time"));
        comment.setAuthorId(rs.getInt("author_id"));
        comment.setBlocked(rs.getBoolean("is_blocked"));
        return comment;
    }
}
