package application.dao.mappers;

import application.models.dto.NotificationsSettingsDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationsSettingsMapper implements RowMapper<NotificationsSettingsDto> {
    @Override
    public NotificationsSettingsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        NotificationsSettingsDto notificationsSettingsDto = new NotificationsSettingsDto();
        notificationsSettingsDto.setNotificationType(rs.getString("code"));
        notificationsSettingsDto.setEnable(rs.getBoolean("status"));
        return notificationsSettingsDto;
    }
}
