package application.dao.mappers;

import application.models.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setPostText(rs.getString("post_text"));
        post.setTitle(rs.getString("title"));
        post.setAuthorId(rs.getInt("author_id"));
        post.setTime(rs.getLong("time"));
        return post;
    }
}
