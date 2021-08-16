package application.dao;

import application.models.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoLike {

    private final JdbcTemplate jdbcTemplate;

    public Integer getCountLike(Integer id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM post_like WHERE post_id = ?", new Object[]{id}, Integer.class);
    }

    public List<String> getUsersLike(Integer id) {
        return jdbcTemplate.queryForList("SELECT person_id FROM post_like WHERE post_id = ?", new Object[]{id}, String.class);
    }

    public void save(Like like) {
        jdbcTemplate.update(("INSERT INTO post_like (time, person_id, post_id) VALUES (?, ?, ?)"),
                like.getTime(),
                like.getPersonId(),
                like.getPostId());
    }

    public void deleteByPersonId(int id){
        jdbcTemplate.update("DELETE FROM post_like WHERE person_id = ?", id);
    }
   // public void delete(int)
}
