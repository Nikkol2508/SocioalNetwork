package application.dao;

import application.dao.mappers.TagMapper;
import application.models.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoTag {

    private final JdbcTemplate jdbcTemplate;

    public Tag findByID(int id) {

        return jdbcTemplate.query("SELECT * FROM tag WHERE id = ?", new Object[]{id}, new TagMapper()).stream()
                .findAny().orElse(null);
    }

    public Tag findTagByName(String tagName) {

        return jdbcTemplate.query("SELECT * FROM tag WHERE tag = ?", new Object[]{tagName}, new TagMapper())
                .stream().findAny().orElse(null);
    }

    public List<String> getTagsByPostId(Integer id) {

        return jdbcTemplate.queryForList("SELECT tag.tag FROM tag join post2tag ON post2tag.tag_id = tag.id " +
                "WHERE post2tag.post_id = ?", new Object[]{id}, String.class);
    }

    public List<Tag> getAll() {

        return jdbcTemplate.query("SELECT * FROM tag", new TagMapper());
    }

    public void save(String tag) {

        jdbcTemplate.update("INSERT INTO tag (tag) VALUES (?)", tag);
    }

    public void attachTag2Post (int tagId, int postId) {

        jdbcTemplate.update(("INSERT INTO post2tag (tag_id, post_id) VALUES (?, ?)"), tagId, postId);

    }

    public void detachTag2Post (int tagId, int postId) {

        jdbcTemplate.update("DELETE FROM post2tag WHERE tag_id = " + tagId + " AND post_id = " + postId);

    }

    public void delete(int id) {

        jdbcTemplate.update("DELETE FROM tag WHERE id = ?", id);
    }
}
