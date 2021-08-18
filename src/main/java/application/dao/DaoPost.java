package application.dao;

import application.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoPost implements Dao<Post> {

    private final JdbcTemplate jdbcTemplate;

//    private final static String SQL_INSERT_POST = "INSERT INTO post (title, text, author_id, time) " +
//            "VALUES (?, ?, ?, ?, ?)";
//    private final static String SQL_GET_ALL_POSTS = "SELECT * FROM post ORDER BY time desc";

    //    @Override
//    public Person get(int id) {
//        return jdbcTemplate.query(SQL_FIND_PERSON_BY_ID, new Object[]{id},new PersonMapper()).stream().findAny().orElse(null);
//    }
    @Override
    public Post get(int id) {
        return jdbcTemplate.query("SELECT * FROM post WHERE id = ?", new Object[]{id}, new PostMapper()).stream().findAny().orElse(null);
    }

    @Override
    public List<Post> getAll() {
        return jdbcTemplate.query("SELECT * FROM post ORDER BY time desc", new PostMapper());
    }

    @Override
    public void save(Post post) {
        jdbcTemplate.update("INSERT INTO post (time, id, author_id, post_text, title, is_blocked) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                post.getTime(),
                post.getId(),
                post.getAuthorId(),
                post.getPostText(),
                post.getTitle(),
                post.isBlocked());
    }

    @Override
    public void update(Post post) {

    }

    @Override
    public void delete(Post post) {

    }


//    public int save(Post post,int authorId, String text, String title, long time, Boolean isBlocked) {
////        post.setTitle(title);
////        post.setPostText(text);
////        post.setTime(time);
////        post.setAuthor(authorId);
////        post.setBlocked(isBlocked);
////        Post newPost = null;
//       return post.getId();
//    }

    public void update(@PathVariable int id, String text, String title, long time) {
//        Optional<Post> postOptional = null;
//        Post changedPost = postOptional.get();
//        if (!postOptional.isPresent()) {
//            System.out.println("not found");
//        } else {
//            changedPost.setPostText(text);
//            changedPost.setTitle(title);
//            changedPost.setTime(time);
//        }
    }

    public void delete(int id) {
//        postRepository.deleteById(id);
    }

    public void deleteGoalList() {
//        postRepository.deleteAll();
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
}
