package application.dao;

import application.models.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaoPost {
    private final JdbcTemplate jdbcTemplate;
    private final static String SQL_INSERT_POST = "INSERT INTO post (title, text, author_id, time) " +
            "VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_GET_ALL_POSTS = "SELECT * FROM post ORDER BY time desc";

    public Optional<Post> get(int id) {
        Optional<Post> postOptional = Optional.empty();
        return postOptional;
    }

    public List<Post> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_POSTS, new PostMapper());
    }

    public int save(Post post,int authorId, String text, String title, long time, Boolean isBlocked) {
//        post.setTitle(title);
//        post.setPostText(text);
//        post.setTime(time);
//        post.setAuthor(authorId);
//        post.setBlocked(isBlocked);
//        Post newPost = null;
       return post.getId();
    }

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
}
