package application.dao;

import application.dao.mappers.NotificationMapper;
import application.dao.mappers.NotificationsSettingsMapper;
import application.models.Notification;
import application.models.NotificationType;
import application.models.dto.NotificationsSettingsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class DaoNotification {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_INTO_NOTIFICATIONS = "INSERT INTO notification " +
            "(send_time, person_id, entity_id, contact, src_person_id, type, name) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public List<Notification> getUserNotifications(int id) {

        log.info("getUserNotifications(): start():");
        log.debug("getUserNotifications(): userId = {}", id);
        String selectNotifications = "SELECT * FROM notification WHERE person_id = ? AND name != 'READ'";
        List<Notification> notifications = jdbcTemplate.query(selectNotifications, new Object[]{id}, new NotificationMapper());
        log.debug("getUserNotifications(): notifications = {}", notifications);
        log.info("getUserNotifications(): finish():");
        return notifications;
    }

    public List<NotificationsSettingsDto> getNotificationsSettings(int id) {

        log.info("getNotificationsSettings(): start():");
        log.debug("getNotificationsSettings(): id = {}", id);
        String select = "SELECT code, status FROM notification_setting_type JOIN notification_settings ns " +
                "ON notification_setting_type.id = ns.type_id WHERE person_id = ?";
        List<NotificationsSettingsDto> notificationsSettingsDtoList = jdbcTemplate.query(select, new Object[]{id}, new NotificationsSettingsMapper());
        log.debug("getNotificationsSettings(): notificationsSettingsDtoList = {}", notificationsSettingsDtoList);
        log.info("getNotificationsSettings(): finish():");
        return notificationsSettingsDtoList;
    }

    @Transactional
    public void setDefaultSettings(int id) {
        log.info("setDefaultSettings(): start():");
        log.debug("setDefaultSettings(): id = {}", id);
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
        log.info("setDefaultSettings(): finish():");
    }

    public void addNotification(int id, int srsId, long sentTime, int entityId, String contact,
                                String type, String... name) {

        log.info("addNotification(): start():");
        log.debug("addNotification(): id = {}, srsId = {}, sentTime = {}, entityId = {}," +
                " contact = {}, type = {}, name = {}", id, srsId, sentTime, entityId, contact, type, name);
               jdbcTemplate.update(INSERT_INTO_NOTIFICATIONS, sentTime, id, entityId, contact, srsId,
                type, name.length == 0 ? "" : name[0]);
        log.info("addNotification(): finish():");
    }

    public void addNotificationsForFriends(List<Integer> ids, int srsId, long sentTime, int entityId, String contact,
                                           String type, String name) {
        log.info("addNotificationsForFriends(): start():");
        log.debug("addNotificationsForFriends(): ids = {}, srsId = {}, sentTime = {}, entityId = {}," +
                " contact = {}, type = {}, name = {}", ids, srsId, sentTime, entityId, contact, type, name);
        jdbcTemplate.batchUpdate(INSERT_INTO_NOTIFICATIONS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, sentTime);
                ps.setInt(2, ids.get(i));
                ps.setInt(3, entityId);
                ps.setString(4, contact);
                ps.setInt(5, srsId);
                ps.setString(6, type);
                ps.setString(7, name);
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
        log.info("addNotificationsForFriends(): start():");
    }

    public void readNotifications(int id) {

        log.info("readNotifications(): start():");
        log.debug("readNotifications(): id = {}", id);
        String update = "UPDATE notification SET name = 'READ' WHERE person_id = ?";
        jdbcTemplate.update(update, id);
        log.info("readNotifications(): finish():");
    }

    public void setSettings(int id, NotificationType notificationType, boolean enable) {

        log.info("setSettings(): start():");
        log.debug("setSettings(): id = {}, notificationType = {}, enable = {}", id, notificationType, enable);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("notification_setting_type")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> param = new HashMap<>();
        param.put("code", notificationType);
        param.put("status", enable);
        String update = "UPDATE notification_settings SET type_id = ? WHERE person_id = ? AND " +
                "type_id = (SELECT notification_setting_type.id FROM notification_setting_type " +
                "WHERE notification_setting_type.code = ? AND notification_setting_type.id = notification_settings.type_id)";
        jdbcTemplate.update(update, simpleJdbcInsert.executeAndReturnKey(param).intValue(), id, notificationType.toString());
        log.info("setSettings(): finish():");
        jdbcTemplate.update(update, simpleJdbcInsert.executeAndReturnKey(param).intValue(), id, notificationType.toString());
    }

    public void readNotificationForId(int id) {

        log.info("readNotificationForId(): start():");
        log.debug("readNotificationForId(): id = {}", id);
        String update = "DELETE FROM notification WHERE id = ?";
        jdbcTemplate.update(update, id);
        log.info("readNotificationForId(): finish():");
    }

    public void addFriendBirthdateNotification(long currentTimeMillis, int id, List<Integer> idList,
                                               String email, NotificationType friendBirthday, String... name) {
        log.info("addFriendBirthdateNotification(): start():");
        log.debug("addFriendBirthdateNotification(): currentTimeMillis = {}, id = {}, idList = {}, email = {}," +
                " friendBirthday = {}, name = {}", currentTimeMillis, id, idList, email, friendBirthday, name);
        jdbcTemplate.batchUpdate(INSERT_INTO_NOTIFICATIONS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, currentTimeMillis);
                ps.setInt(2, id);
                ps.setInt(3, idList.get(i));
                ps.setString(4, email);
                ps.setInt(5, idList.get(i));
                ps.setString(6, friendBirthday.toString());
                ps.setString(7, name.length == 0 ? "" : name[0]);
            }

            @Override
            public int getBatchSize() {
                return idList.size();
            }
        });
        log.info("addFriendBirthdateNotification(): finish():");
    }
}
