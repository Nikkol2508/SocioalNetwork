package application.dao;

import application.dao.mappers.PostMapper;
import application.models.NotificationType;
import application.models.Person;
import application.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoPost implements Dao<Post> {
    private final JdbcTemplate jdbcTemplate;
    private final DaoNotification daoNotification;
    private final DaoPerson daoPerson;

    @Override
    public Post getById(int id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE id = ?", new Object[]{id}, new PostMapper())
                .stream().findAny().orElse(null);
    }

    @Override
    public List<Post> getAll() {
        return jdbcTemplate.query("SELECT * FROM post WHERE time < " +
                System.currentTimeMillis() + " AND is_blocked = false ORDER BY time desc", new PostMapper());
    }

    @Override
    public void save(Post post) {
        jdbcTemplate.update("INSERT INTO post (time, author_id, post_text, title, is_blocked) " +
                        "VALUES (?, ?, ?, ?, ?)",
                post.getTime(),
                post.getAuthorId(),
                post.getPostText(),
                post.getTitle(),
                post.isBlocked());
        for (Person person : daoPerson.getFriends(daoPerson.getAuthPerson().getId())) {
            daoNotification.addNotification(person.getId(), post.getTime(), post.getId(),
                    daoPerson.getById(post.getAuthorId()).getEmail(), NotificationType.POST.toString(), post.getTitle());
        }
    }

    public Post savePost (Post post) {
        String query = "INSERT INTO post (time, author_id, post_text, title, is_blocked) " +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, post.getTime());
                    ps.setInt(2, post.getAuthorId());
                    ps.setString(3, post.getPostText());
                    ps.setString(4, post.getTitle());
                    ps.setBoolean(5, post.isBlocked());
                    return ps;},
                key);
        return getById((int) key.getKeys().get("id"));
    }

    @Override
    public void update(Post post) {
        jdbcTemplate.update("UPDATE post SET time=?, author_id=?, post_text=?, title=?, is_blocked=? WHERE id=?",
                post.getTime(),
                post.getAuthorId(),
                post.getPostText(),
                post.getTitle(),
                post.isBlocked(),
                post.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM post WHERE id = "+ id);
    }

    public void deleteByAuthorId(int id){
        jdbcTemplate.update("DELETE FROM post WHERE author_id = ?", id);
    }

    public List<Post> getPostsByTitle(String text) {
        String query = "select * from post where title LIKE concat(concat('%',?), '%')";
        return jdbcTemplate.query(query,
                new Object[]{text},
                        new PostMapper());
    }

    public List<Post> getPosts(String text, Integer authorId, Long dateFrom, Long dateTo) {

        String query = "select * from post where " +
                "post_text LIKE concat(concat('%',?), '%')" +
                "and (author_id  = ? or ?::int is null) " +
                "and (time >= ? or ?::bigint is null) " +
                "and (time <= ? or ?::bigint is null)";

        return jdbcTemplate.query(query,
                new Object[]{text,
                        authorId, authorId,
                        dateFrom, dateFrom,
                        dateTo, dateTo},
                new PostMapper());
    }

    public List<Post> getAllUsersPosts(int id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE is_blocked = false AND author_id = " + id +
                " ORDER BY time desc", new PostMapper());
    }
}
