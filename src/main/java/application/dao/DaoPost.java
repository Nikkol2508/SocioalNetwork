package application.dao;

import application.dao.mappers.PostMapper;
import application.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DaoPost {

    private final JdbcTemplate jdbcTemplate;

    public Post getById(int id) {

        return jdbcTemplate.query("SELECT * FROM post WHERE id = ?", new Object[]{id}, new PostMapper())
                .stream().findAny().orElse(null);
    }

    public List<Post> getAll() {

        return jdbcTemplate.query("SELECT * FROM post WHERE time < " +
                System.currentTimeMillis() + " AND is_blocked = false ORDER BY time desc", new PostMapper());
    }

    public void save(Post post) {

        jdbcTemplate.update("INSERT INTO post (time, author_id, post_text, title, is_blocked) " +
                        "VALUES (?, ?, ?, ?, ?)", post.getTime(), post.getAuthorId(), post.getPostText(),
                post.getTitle(), post.isBlocked());

    }

    public Post savePost(Post post) {

        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("post").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", post.getTime());
        parameters.put("author_id", post.getAuthorId());
        parameters.put("post_text", post.getPostText());
        parameters.put("title", post.getTitle());
        parameters.put("is_blocked", post.isBlocked());
        return getById(sji.executeAndReturnKey(parameters).intValue());
    }

    public void update(Post post) {

        jdbcTemplate.update("UPDATE post SET time = ?, author_id = ?, post_text = ?, title = ?, is_blocked = ? " +
                        "WHERE id=?", post.getTime(), post.getAuthorId(), post.getPostText(), post.getTitle(),
                post.isBlocked(), post.getId());
    }

    public void delete(int id) {

        jdbcTemplate.update("DELETE FROM post WHERE id = "+ id);
    }

    public void deleteByAuthorId(int id){

        jdbcTemplate.update("DELETE FROM post WHERE author_id = ?", id);
    }

    public List<Post> getPosts(String text, String author, Long dateFrom, Long dateTo, List<String> tags) {
        String tagsInStr = tags != null ? String.join("|", tags) : null;
        String query = "SELECT * FROM post_tag_user_view WHERE (post_text ILIKE ? OR title ILIKE ?) " +
                "AND ((first_name ILIKE ? OR ?::text IS NULL) OR (last_name ILIKE ? OR ?::text IS NULL))" +
                "AND (time >= ? OR ?::bigint IS NULL) AND (time <= ? OR ?::bigint IS NULL) " +
                "AND (tag ~* ? OR ?::text IS NULL)";

        return jdbcTemplate.query(query, new Object[]{prepareParam(text), prepareParam(text),
                prepareParam(author), author, prepareParam(author), author, dateFrom, dateFrom, dateTo, dateTo,
                tagsInStr, tagsInStr}, new PostMapper());

    }

    private String prepareParam(String param) {
        return "%" + param + "%";
    }

    public List<Post> getAllUsersPosts(int id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE is_blocked = false AND author_id = " + id +
                " ORDER BY time desc", new PostMapper());
    }
}
