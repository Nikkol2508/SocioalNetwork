package application.dao;

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
        return jdbcTemplate.query("SELECT * FROM tag WHERE id = ?", new Object[]{id}, new TagMapper()).stream().findAny().orElse(null);
    }

    public List<String> getTagsByPostId(Integer id) {
        return jdbcTemplate.queryForList("SELECT tag.tag FROM tag join post2tag on post2tag.id = tag.id where post2tag.post_id = ?", new Object[]{id}, String.class);
    }

    public List<Tag> getAll() {
        return jdbcTemplate.query("SELECT * FROM tag", new TagMapper());
    }

    public void save(Tag tag) {
        jdbcTemplate.update(("INSERT INTO tag (tag) VALUES (?)"),
                tag.getTag());
    }

//    public void delete(int id) {
//        jdbcTemplate.d
//    }
}
