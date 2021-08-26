package application.dao;

import application.dao.mappers.NotificationMapper;
import application.dao.mappers.NotificationsSettingsMapper;
import application.models.Notification;
import application.models.dto.NotificationsSettingsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class DaoNotification {
    private final JdbcTemplate jdbcTemplate;

    public List<Notification> getUserNotifications(int id) {
        String selectNotifications = "SELECT * FROM notification WHERE person_id = ?";
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

    public void setSettings(int personId, String code, boolean status) {
        String updateSettings = "UPDATE notification_setting_type set status = ? WHERE code = ? " +
                "AND id IN (SELECT type_id from notification_settings WHERE person_id = ?)";
        jdbcTemplate.update(updateSettings, status, code, personId);
    }

    public void addNotification(int id, long sentTime, int entityId, String contact,
                                String type, String... name) {
        String insertIntoNotificationsStatus = "INSERT INTO notification_type (code, name) VALUES (?, ?)";
        jdbcTemplate.update(insertIntoNotificationsStatus, type,
                name[0]);
        String insertIntoNotifications = "INSERT INTO notification (type_id, send_time, person_id, entity_id, contact)" +
                " VALUES ((SELECT max(notification_type.id) FROM notification_type), ?, ?, ?, ?)";
        jdbcTemplate.update(insertIntoNotifications, sentTime, id, entityId, contact);
    }
}
