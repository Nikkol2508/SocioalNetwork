package application.dao;

import application.dao.mappers.PostMapper;
import application.models.Post;
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
public class DaoPost {

    private final JdbcTemplate jdbcTemplate;

    public Post getById(int id) {

        log.info("getById(): start():");
        log.debug("getById(): postId = {}", id);
        Post post = jdbcTemplate.query("SELECT * FROM post WHERE id = ?", new Object[]{id}, new PostMapper())
                .stream().findAny().orElse(null);
        log.debug("getById(): post = {}", post);
        log.info("getById(): finish():");
        return post;
    }

    public List<Post> getAll() {

        log.info("getAll(): start():");
        List<Post> posts = jdbcTemplate.query("SELECT * FROM post WHERE time < " +
                System.currentTimeMillis() + " AND is_blocked = false ORDER BY time desc", new PostMapper());
        log.debug("getAll(): posts = {}", posts);
        log.info("getAll(): finish():");
        return posts;
    }

    public void save(Post post) {

        log.info("save(): start():");
        log.debug("save(): post = {}", post);
        jdbcTemplate.update("INSERT INTO post (time, author_id, post_text, title, is_blocked) " +
                        "VALUES (?, ?, ?, ?, ?)", post.getTime(), post.getAuthorId(), post.getPostText(),
                post.getTitle(), post.isBlocked());
        log.info("save(): finish():");
    }

    public Post savePost(Post post) {

        log.info("savePost(): start():");
        log.debug("savePost(): post = {}", post);
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("post").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", post.getTime());
        parameters.put("author_id", post.getAuthorId());
        parameters.put("post_text", post.getPostText());
        parameters.put("title", post.getTitle());
        parameters.put("is_blocked", post.isBlocked());
        Post postById = getById(sji.executeAndReturnKey(parameters).intValue());
        log.info("savePost(): finish():");
        return postById;
    }

    public void update(Post post) {

        log.info("update(): start():");
        log.debug("update(): post = {}", post);
        jdbcTemplate.update("UPDATE post SET time = ?, author_id = ?, post_text = ?, title = ?, is_blocked = ? " +
                        "WHERE id=?", post.getTime(), post.getAuthorId(), post.getPostText(), post.getTitle(),
                post.isBlocked(), post.getId());
        log.info("update(): finish():");
    }

    public void delete(int id) {

        log.info("delete(): start():");
        log.debug("delete(): postId = {}", id);
        jdbcTemplate.update("DELETE FROM post WHERE id = "+ id);
        log.info("delete(): finish():");
    }

    public void deleteByAuthorId(int id){

        log.info("deleteByAuthorId(): start():");
        log.debug("deleteByAuthorId(): authorId = {}", id);
        jdbcTemplate.update("DELETE FROM post WHERE author_id = ?", id);
        log.info("deleteByAuthorId(): finish():");
    }

    public List<Post> getPosts(String text, String author, Long dateFrom, Long dateTo, List<String> tags) {

        log.info("getPosts(): start():");
        log.debug("getPosts(): text = {}, author = {}, dateFrom = {}, dateTo = {}, tags = {}", text, author, dateFrom, dateTo, tags);
        String tagsInStr = tags != null ? String.join("|", tags) : null;
        String query = "SELECT * FROM post_tag_user_view WHERE (post_text ILIKE ? OR title ILIKE ?) " +
                "AND ((first_name ILIKE ? OR ?::text IS NULL) OR (last_name ILIKE ? OR ?::text IS NULL))" +
                "AND (time >= ? OR ?::bigint IS NULL) AND (time <= ? OR ?::bigint IS NULL) " +
                "AND (tag ~* ? OR ?::text IS NULL)";
        List <Post> posts = jdbcTemplate.query(query, new Object[]{prepareParam(text), prepareParam(text),
                prepareParam(author), author, prepareParam(author), author, dateFrom, dateFrom, dateTo, dateTo,
                tagsInStr, tagsInStr}, new PostMapper());
        log.debug("getPosts(): posts = {}", posts);
        log.info("getPosts(): finish():");

        return posts;

    }

    private String prepareParam(String param) {
        return "%" + param + "%";
    }

    public List<Post> getAllUsersPosts(int id) {

        log.info("getPosts(): start():");
        log.debug("getPosts(): authorId = {}", id);
        List<Post> posts = jdbcTemplate.query("SELECT * FROM post WHERE is_blocked = false AND author_id = " + id +
                " ORDER BY time desc", new PostMapper());
        log.debug("getPosts(): posts = {}", posts);
        log.info("getPosts(): start():");
        return posts;
    }
}
