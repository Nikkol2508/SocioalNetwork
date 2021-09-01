package application.dao;

import application.dao.mappers.LikeMapper;
import application.models.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoLike {

    private final JdbcTemplate jdbcTemplate;

    public Integer getCountLike(Integer id, String type) {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM post_like WHERE item_id = ? AND type = ?",
                new Object[]{id, type}, Integer.class);
    }

    public List<String> getUsersLike(Integer id, String type) {
        return jdbcTemplate.queryForList("SELECT person_id FROM post_like WHERE item_id = ? AND type = ?",
                new Object[]{id, type}, String.class);
    }

    public Integer getMyLike(int item_id, String type, int person_id) {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM post_like WHERE item_id = ? AND type = ? AND person_id = ?",
                new Object[]{item_id, type, person_id}, Integer.class);
    }

    public void save(Like like) {
        jdbcTemplate.update(("INSERT INTO post_like (time, person_id, item_id, type) VALUES (?, ?, ?, ?)"),
                like.getTime(),
                like.getPersonId(),
                like.getItemId(),
                like.getType());
    }

    public void deleteByPersonId(int id){
        jdbcTemplate.update("DELETE FROM post_like WHERE item_id = ?", id);
    }

    public void delete(int item_id, String type, int person_id) {
        jdbcTemplate.update("DELETE FROM post_like WHERE item_id = ? AND type = ? AND person_id = ?",
                new Object[]{item_id, type, person_id});
    }

    public List<Like> getLikeByPost (int itemId, String type) {
        return jdbcTemplate.query("SELECT * FROM post_like WHERE item_id = " + itemId + " AND type = '" + type + "'", new LikeMapper());
    }
}
