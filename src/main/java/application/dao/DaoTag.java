package application.dao;

import application.dao.mappers.TagMapper;
import application.models.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoTag {

    private final JdbcTemplate jdbcTemplate;

    public Tag findByID(int id) {

        log.info("findByID(): start():");
        log.debug("findByID(): tagId = {}", id);
        Tag tag = jdbcTemplate.query("SELECT * FROM tag WHERE id = ?", new Object[]{id}, new TagMapper()).stream()
                .findAny().orElse(null);
        log.debug("findByID(): tag = {}", tag);
        log.info("findByID(): finish():");
        return tag;
    }

    public Tag findTagByName(String tagName) {

        log.info("findTagByName(): start():");
        log.debug("findTagByName(): tagName = {}", tagName);
        Tag tag = jdbcTemplate.query("SELECT * FROM tag WHERE tag = ?", new Object[]{tagName}, new TagMapper())
                .stream().findAny().orElse(null);
        log.debug("findTagByName(): tag = {}", tag);
        log.info("findTagByName(): finish():");
        return tag;
    }

    public List<String> getTagsByPostId(Integer id) {

        log.info("getTagsByPostId(): start():");
        log.debug("getTagsByPostId(): postId = {}", id);
        List <String> tags = jdbcTemplate.queryForList("SELECT tag.tag FROM tag join post2tag ON post2tag.tag_id = tag.id " +
                "WHERE post2tag.post_id = ?", new Object[]{id}, String.class);
        log.debug("getTagsByPostId(): tags = {}", tags);
        log.info("getTagsByPostId(): finish():");
        return tags;
    }

    public List<Tag> getAll() {

        log.info("getAll(): start():");
        List<Tag> tags = jdbcTemplate.query("SELECT * FROM tag", new TagMapper());
        log.debug("getAll(): tags = {}", tags);
        log.info("getAll(): finish():");
        return tags;
    }

    public void save(String tag) {
        log.info("save(): start():");
        log.debug("save(): tag = {}", tag);
        jdbcTemplate.update("INSERT INTO tag (tag) VALUES (?)", tag);
        log.info("save(): finish():");
    }

    public void attachTag2Post (int tagId, int postId) {

        log.info("attachTag2Post(): start():");
        log.debug("attachTag2Post(): tagId = {}, postId = {}", tagId, postId);
        jdbcTemplate.update(("INSERT INTO post2tag (tag_id, post_id) VALUES (?, ?)"), tagId, postId);
        log.info("attachTag2Post(): finish():");
    }

    public void detachTag2Post (int tagId, int postId) {

        log.info("detachTag2Post(): start():");
        log.debug("detachTag2Post(): tagId = {}, postId = {}", tagId, postId);
        jdbcTemplate.update("DELETE FROM post2tag WHERE tag_id = " + tagId + " AND post_id = " + postId);
        log.info("detachTag2Post(): finish():");
    }

    public void delete(int id) {

        log.info("delete(): start():");
        log.debug("delete(): tagId = {}", id);
        jdbcTemplate.update("DELETE FROM tag WHERE id = ?", id);
        log.info("delete(): finish():");
    }
}
