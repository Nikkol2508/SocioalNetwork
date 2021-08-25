package application.dao;

import application.dao.mappers.NotificationMapper;
import application.dao.mappers.NotificationsSettingsMapper;
import application.models.Notification;
import application.models.NotificationType;
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
        return jdbcTemplate.query(selectNotifications, new Object[]{id} ,new NotificationMapper());
    }

    public void addNotificationForFriendRequest(int id, long sentTime, int entityId, String contact) {
        String insertIntoNotificationsStatus = "INSERT INTO notification_type (code, name) VALUES (?, ?)";
        jdbcTemplate.update(insertIntoNotificationsStatus, NotificationType.FRIEND_REQUEST.toString(),
                "Заявка на добавление в друзья");
        String insertIntoNotifications = "INSERT INTO notification (type_id, send_time, person_id, entity_id, contact)" +
                " VALUES ((SELECT max(notification_type.id) FROM notification_type), ?, ?, ?, ?)";
        jdbcTemplate.update(insertIntoNotifications, sentTime, id, entityId, contact);
    }

    public List<NotificationsSettingsDto> getNotificationsSettings (int id) {
        String select = "SELECT code, status FROM notification_setting_type JOIN notification_settings ns " +
                "on notification_setting_type.id = ns.type_id WHERE person_id = ?";
        return jdbcTemplate.query(select, new Object[]{id}, new NotificationsSettingsMapper());
    }

    public List<NotificationsSettingsDto> setNotificationsSettings (int id, String type, boolean status) {
        String select = "SELECT code, status FROM notification_setting_type JOIN notification_settings ns " +
                "on notification_setting_type.id = ns.type_id WHERE person_id = ?";
        return jdbcTemplate.query(select, new Object[]{id}, new NotificationsSettingsMapper());
    }
}
