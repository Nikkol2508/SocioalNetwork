package application.dao;

import application.dao.mappers.NotificationMapper;
import application.dao.mappers.NotificationsSettingsMapper;
import application.models.Notification;
import application.models.dto.NotificationsSettingsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import liquibase.pro.packaged.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
@Slf4j
public class DaoNotification {
    private final JdbcTemplate jdbcTemplate;

    public List<Notification> getUserNotifications(int id) {
        String selectNotifications = "SELECT * FROM notification JOIN notification_type nt on nt.id = notification.type_id" +
                " WHERE person_id = ? AND name != 'READ'";
        return jdbcTemplate.query(selectNotifications, new Object[]{id}, new NotificationMapper());
    }

    public List<NotificationsSettingsDto> getNotificationsSettings(int id) {
        String select = "SELECT code, status FROM notification_setting_type JOIN notification_settings ns " +
                "on notification_setting_type.id = ns.type_id WHERE person_id = ?";
        return jdbcTemplate.query(select, new Object[]{id}, new NotificationsSettingsMapper());
    }

    public void setDefaultSettings(int id, String code) {
        String insertSettingsType = "INSERT INTO notification_setting_type (code, status) VALUES (?, DEFAULT)";
        jdbcTemplate.update(insertSettingsType, code);
        String insertNotificationsType = "INSERT INTO notification_settings (person_id, type_id) VALUES (?," +
                " (SELECT max(notification_setting_type.id) FROM notification_setting_type))";
        jdbcTemplate.update(insertNotificationsType, id);
    }

    public void addNotification(int id, int srsId, long sentTime, int entityId, String contact,
                                String type, String... name) {
        String insertIntoNotificationsStatus = "INSERT INTO notification_type (code, name) VALUES (?, ?)";
        jdbcTemplate.update(insertIntoNotificationsStatus, type,
                name);
        String insertIntoNotifications = "INSERT INTO notification (type_id, send_time, person_id, entity_id, contact, src_person_id)" +
                " VALUES ((SELECT max(notification_type.id) FROM notification_type), ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertIntoNotifications, sentTime, id, entityId, contact, srsId);
    }

    public void readNotifications(int id) {
        String update = "UPDATE notification_type SET name = 'READ' WHERE id IN (SELECT type_id FROM notification" +
                " WHERE person_id = ?)";
        jdbcTemplate.update(update, id);
    }

    public String getNotificationName(int id) {
        try {
            String select = "SELECT name FROM notification_type JOIN notification n on notification_type.id = n.type_id" +
                    " WHERE n.id = ?";
            return jdbcTemplate.queryForObject(select, new Object[]{id}, String.class)
                    .replaceAll("(^[{}\"]{0,2})|([{}\"]{0,2}$)", "");
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    public String getNotificationType(int id) {
        String s = "SELECT code FROM notification_type WHERE id = ?";
        return jdbcTemplate.queryForObject(s, new Object[]{id}, String.class);
    }
}
