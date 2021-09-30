package application.dao;

import application.dao.mappers.PostMapper;
import application.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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

        jdbcTemplate.update("DELETE FROM post WHERE id = " + id);
    }

    public void deleteByAuthorId(int id) {

        jdbcTemplate.update("DELETE FROM post WHERE author_id = ?", id);
    }

    public List<Post> getPosts(String text, String author, Long dateFrom, Long dateTo, List<String> tags) {

        String textQuery = "SELECT * FROM post WHERE (post_text ILIKE ? OR title ILIKE ?) " +
                "AND (time >= ? OR ?::bigint IS NULL) AND (time <= ? OR ?::bigint IS NULL)AND post.is_blocked = false";
        List<Post> posts = jdbcTemplate.query(textQuery, new Object[]{prepareParam(text), prepareParam(text), dateFrom,
                dateFrom, dateTo, dateTo}, new PostMapper());
        if (posts.isEmpty()) {
            return new ArrayList<>();
        }
        String tagsInStr = tags != null ? String.join("|", tags) : null;
        Set<Integer> postIdsFromTagQuery = new HashSet<>(jdbcTemplate.queryForList("SELECT post_id FROM post2tag " +
                        "LEFT JOIN tag ON tag.id = post2tag.tag_id WHERE (tag ~* ? OR ?::text IS NULL)",
                new Object[]{tagsInStr, tagsInStr}, Integer.class));
        String authorIds = posts.stream().map(post -> String.valueOf(post.getAuthorId())).collect(
                Collectors.joining(","));
        Set<Integer> personIdsQuery = new HashSet<>(jdbcTemplate.queryForList("SELECT id FROM " +
                "person WHERE ((first_name ILIKE ? OR ?::text IS NULL) OR (last_name ILIKE ? " +
                "OR ?::text IS NULL)) AND id IN (" + authorIds + ")", new Object[]{prepareParam(author), author,
                prepareParam(author), author}, Integer.class));
        Set<Integer> resultSetPostIds = new HashSet<>(postIdsFromTagQuery);
        resultSetPostIds.retainAll(posts.stream().filter(p -> personIdsQuery.contains(p.getAuthorId()))
                .map(Post::getId).collect(Collectors.toSet()));
        return posts.stream().filter(post -> resultSetPostIds.contains(post.getId())).collect(Collectors.toList());
    }

    private String prepareParam(String param) {
        return "%" + param + "%";
    }

    public List<Post> getAllUsersPosts(int id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE is_blocked = false AND author_id = " + id +
                " ORDER BY time desc", new PostMapper());
    }
}
