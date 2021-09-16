package application.dao.mappers;

import application.models.Notification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements RowMapper<Notification> {
    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setType(rs.getString("type")
                .replaceAll("(^[{}\"]{0,2})|([{}\"]{0,2}$)", ""));
        notification.setSrcPersonId(rs.getInt("src_person_id"));
        notification.setSentTime(rs.getLong("send_time"));
        notification.setEntityId(rs.getInt("entity_id"));
        notification.setContact(rs.getString("contact"));
        notification.setName(rs.getString("name"));
        return notification;
    }
}
