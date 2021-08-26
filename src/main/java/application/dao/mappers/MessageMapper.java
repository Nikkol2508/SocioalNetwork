package application.dao.mappers;

import application.models.Message;
import application.models.ReadStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setTime(rs.getLong("time"));
        message.setAuthorId(rs.getInt("author_id"));
        message.setRecipientId(rs.getInt("recipient_id"));
        message.setMessageText(rs.getString("message_text"));
        message.setReadStatus(ReadStatus.valueOf(rs.getString("read_status")));
        message.setDialogId(rs.getInt("dialog_id"));
        return message;
    }
}
