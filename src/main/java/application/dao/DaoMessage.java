package application.dao;

import application.dao.mappers.MessageMapper;
import application.models.Dialog;
import application.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DaoMessage {

    private final JdbcTemplate jdbcTemplate;

    public Message getById(int id) {

        String selectMessageById = "SELECT * FROM message WHERE id = ?";
        return jdbcTemplate.query(selectMessageById, new Object[]{id}, new MessageMapper()).stream().findAny()
                .orElse(null);
    }

    public Message saveAndReturnMessage(Message message) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("message")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", System.currentTimeMillis());
        parameters.put("author_id", message.getAuthorId());
        parameters.put("recipient_id", message.getRecipientId());
        parameters.put("message_text", message.getMessageText());
        parameters.put("read_status", message.getReadStatus().toString());
        parameters.put("dialog_id", message.getDialogId());
        return getById(sji.executeAndReturnKey(parameters).intValue());
    }

    public Message updateAndReturnMessage(Message message) {

        // при необходимости изменить время (time)
        jdbcTemplate.update("UPDATE message SET message_text = ? WHERE id = ? AND dialog_id = ?",
                message.getMessageText(), message.getId(), message.getDialogId());
        return getById(message.getId());
    }

    public void readMessage(int dialogId, int messageId) {

        jdbcTemplate.update("UPDATE message SET read_status = 'READ' WHERE id = ? AND dialog_id = ?",
                messageId, dialogId);
    }

    public void deleteById(int messageId, int dialogId) {

        String deleteMessage = "DELETE FROM message WHERE id = ? AND dialog_id = ?";
        jdbcTemplate.update(deleteMessage, messageId, dialogId);
    }

    public Integer getCountUnreadedMessagesForUser(int id) {

        String getUnreaded = "SELECT count(*) FROM message WHERE recipient_id = ? AND read_status = 'SENT'";
        return jdbcTemplate.queryForObject(getUnreaded, new Object[]{id}, Integer.class);
    }

    public Integer getCountUnreadedMessagesInDialog(int activeUserId, int dialogId) {

        String getUnreaded = "SELECT count(*) FROM message WHERE (recipient_id = ? AND dialog_id = ?) " +
                "AND read_status = 'SENT'";
        return jdbcTemplate.queryForObject(getUnreaded, new Object[]{activeUserId, dialogId}, Integer.class);
    }

    public Message getLastMessageInDialog(Dialog dialog) {

        String getLastMessage = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time DESC LIMIT 1";
        return jdbcTemplate.queryForObject(getLastMessage, new Object[]{dialog.getFirstUserId(),
                        dialog.getSecondUserId(), dialog.getSecondUserId(), dialog.getFirstUserId()},
                new MessageMapper());
    }

    public List<Message> getMessagesInDialog(Dialog dialog) {
        String getMessages = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time";
        return jdbcTemplate.query(getMessages, new Object[]{dialog.getFirstUserId(), dialog.getSecondUserId(),
                        dialog.getSecondUserId(), dialog.getFirstUserId()},
                new MessageMapper());
    }
}
