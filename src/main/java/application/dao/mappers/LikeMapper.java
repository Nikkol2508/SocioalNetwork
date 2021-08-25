package application.dao.mappers;

import application.models.Like;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeMapper implements RowMapper<Like> {

    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {

        Like like = new Like();
        like.setId(rs.getInt("id"));
        like.setTime(rs.getLong("time"));
        like.setPersonId(rs.getInt("person_id"));
        like.setItemId(rs.getInt("item_id"));
        like.setType(rs.getString("type"));
        return like;
    }
}
