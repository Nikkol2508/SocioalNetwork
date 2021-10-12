package application.dao;

import application.dao.mappers.LikeMapper;
import application.models.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoLike {

    private final JdbcTemplate jdbcTemplate;

    public Integer getCountLike(Integer id, String type) {

        log.info("getCountLike(): start():");
        log.debug("getCountLike(): id = {}, type = {}", id, type);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM post_like WHERE item_id = ? AND type = ?",
                new Object[]{id, type}, Integer.class);
        log.debug("getCountLike(): count = {}", count);
        log.info("getCountLike(): finish():");
        return count;
    }

    public List<String> getUsersLike(Integer id, String type) {

        log.info("getUsersLike(): start():");
        log.debug("getUsersLike(): id = {}, type = {}", id, type);
        List<String> likes = jdbcTemplate.queryForList("SELECT person_id FROM post_like WHERE item_id = ? AND type = ?",
                new Object[]{id, type}, String.class);
        log.debug("getUsersLike(): likes = {}", likes);
        log.info("getUsersLike(): start():");
        return likes;
    }

    public Integer getMyLike(int itemId, String type, int personId) {

        log.info("getMyLike(): start():");
        log.debug("getMyLike(): itemId = {}, type = {}, personId = {}", itemId, type, personId);
        Integer likes = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM post_like WHERE item_id = ? AND type = ? AND person_id = ?",
                new Object[]{itemId, type, personId}, Integer.class);
        log.debug("getMyLike(): likes = {}", likes);
        log.info("getMyLike(): finish():");
        return likes;
    }

    public void save(Like like) {
        log.info("save(): start():");
        log.debug("save(): like = {}", like);
        jdbcTemplate.update(("INSERT INTO post_like (time, person_id, item_id, type) VALUES (?, ?, ?, ?)"),
                like.getTime(), like.getPersonId(), like.getItemId(), like.getType());
        log.info("save(): finish():");
    }

    public void deleteByPersonId(int id){
        log.info("deleteByPersonId(): start():");
        log.debug("deleteByPersonId(): id = {}", id);
        jdbcTemplate.update("DELETE FROM post_like WHERE item_id = ?", id);
        log.info("deleteByPersonId(): finish():");
    }

    public void delete(int itemId, String type, int personId) {
        log.info("delete(): start():");
        log.debug("delete(): itemId = {}, type = {}, personId = {}", itemId, type, personId);
        jdbcTemplate.update("DELETE FROM post_like WHERE item_id = ? AND type = ? AND person_id = ?", itemId, type,
                personId);
        log.info("delete(): finish():");
    }

    public List<Like> getLikeByPost (int itemId, String type) {
        log.info("getLikeByPost(): start():");
        log.debug("getLikeByPost(): itemId = {}, type = {}", itemId, type);
        List<Like> likeList = jdbcTemplate.query("SELECT * FROM post_like WHERE item_id = " + itemId + " AND type = '" + type + "'",
                new LikeMapper());
        log.debug("getLikeByPost(): likeList = {}", likeList);
        log.info("getLikeByPost(): finish():");
        return likeList;
    }
}
