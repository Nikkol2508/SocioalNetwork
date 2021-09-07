package application.dao;

import application.dao.mappers.NotificationMapper;
import application.dao.mappers.NotificationsSettingsMapper;
import application.models.Notification;
import application.models.NotificationSettingType;
import application.models.dto.NotificationsSettingsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class DaoNotification {

    private final JdbcTemplate jdbcTemplate;

    public List<Notification> getUserNotifications(int id) {

        String selectNotifications = "SELECT * FROM notification JOIN notification_type nt " +
                "ON nt.id = notification.type_id WHERE person_id = ? AND name != 'READ'";
        return jdbcTemplate.query(selectNotifications, new Object[]{id}, new NotificationMapper());
    }

    public List<NotificationsSettingsDto> getNotificationsSettings(int id) {

        String select = "SELECT code, status FROM notification_setting_type JOIN notification_settings ns " +
                "ON notification_setting_type.id = ns.type_id WHERE person_id = ?";
        return jdbcTemplate.query(select, new Object[]{id}, new NotificationsSettingsMapper());
    }

    @Transactional
    public void setDefaultSettings(int id) {
        int[] ids = new int[]{1, 2, 3, 4, 5, 6};
        String insertNotificationsType = "INSERT INTO notification_settings (person_id, type_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(insertNotificationsType, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, id);
                ps.setInt(2, ids[i]);
            }

            @Override
            public int getBatchSize() {
                return ids.length;
            }
        });
    }

    @Transactional
    public void addNotification(int id, int srsId, long sentTime, int entityId, String contact,
                                String type, String... name) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("notification_type").usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("code", type);
        params.put("name", name.length == 0 ? "" : name[0]);
        int newId = simpleJdbcInsert.executeAndReturnKey(params).intValue();

        String insertIntoNotifications = "INSERT INTO notification (type_id, send_time, person_id, entity_id, " +
                "contact, src_person_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertIntoNotifications, newId, sentTime, id, entityId, contact, srsId);
    }

    public void readNotifications(int id) {

        String update = "UPDATE notification_type SET name = 'READ' WHERE id IN (SELECT type_id FROM notification" +
                " WHERE person_id = ?)";
        jdbcTemplate.update(update, id);
    }

    public String getNotificationName(int id) {

        String select = "SELECT name FROM notification_type JOIN notification n " +
                "ON notification_type.id = n.type_id WHERE n.id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(select, new Object[]{id}, String.class))
                .replaceAll("(^[{}\"]{0,2})|([{}\"]{0,2}$)", "");
    }

    public String getNotificationType(int id) {

        String s = "SELECT code FROM notification_type WHERE id = ?";
        return jdbcTemplate.queryForObject(s, new Object[]{id}, String.class);
    }

    public void setSettings(int id, String notification_type, boolean enable) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("notification_setting_type")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> param = new HashMap<>();
        param.put("code", notification_type);
        param.put("status", enable);
        String update = "UPDATE notification_settings SET type_id = ? WHERE person_id = ? AND " +
                "type_id = (SELECT notification_setting_type.id FROM notification_setting_type " +
                "WHERE notification_setting_type.code = ? AND notification_setting_type.id = notification_settings.type_id)";
        jdbcTemplate.update(update, simpleJdbcInsert.executeAndReturnKey(param).intValue(), id, notification_type);
    }
}
