package application.dao;

import application.dao.mappers.MessageMapper;
import application.models.Dialog;
import application.models.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoMessage {

    private final JdbcTemplate jdbcTemplate;

    public Message getById(int id) {

        log.info("getById(): start():");
        log.debug("getById(): id = {}", id);
        String selectMessageById = "SELECT * FROM message WHERE id = ?";
        Message message = jdbcTemplate.query(selectMessageById, new Object[]{id}, new MessageMapper()).stream().findAny()
                .orElse(null);
        log.info("getById(): finish():");
        return message;
    }

    public Message saveAndReturnMessage(Message message) {
        log.info("saveAndReturnMessage(): start():");
        log.debug("saveAndReturnMessage(): message = {}", message);
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("message")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", System.currentTimeMillis());
        parameters.put("author_id", message.getAuthorId());
        parameters.put("recipient_id", message.getRecipientId());
        parameters.put("message_text", message.getMessageText());
        parameters.put("read_status", message.getReadStatus().toString());
        parameters.put("dialog_id", message.getDialogId());
        Message returnMessage = getById(sji.executeAndReturnKey(parameters).intValue());
        log.debug("saveAndReturnMessage(): returnMessage = {}", returnMessage);
        log.info("saveAndReturnMessage(): finish():");
        return returnMessage;
    }

    public Message updateAndReturnMessage(Message message) {
        log.info("updateAndReturnMessage(): start():");
        log.debug("updateAndReturnMessage(): message = {}", message);
        // при необходимости изменить время (time)
        jdbcTemplate.update("UPDATE message SET message_text = ? WHERE id = ? AND dialog_id = ?",
                message.getMessageText(), message.getId(), message.getDialogId());
        Message returnMessage = getById(message.getId());
        log.debug("updateAndReturnMessage(): returnMessage = {}", returnMessage);
        log.info("updateAndReturnMessage(): start():");
        return returnMessage;
    }

    public void readMessage(int dialogId, int messageId) {

        log.info("readMessage(): start():");
        log.debug("readMessage(): dialogId = {}, messageId = {}", dialogId, messageId);
        jdbcTemplate.update("UPDATE message SET read_status = 'READ' WHERE id = ? AND dialog_id = ?",
                messageId, dialogId);
        log.info("readMessage(): finish():");
    }

    public void deleteById(int messageId, int dialogId) {

        log.info("deleteById(): start():");
        log.debug("deleteById(): dialogId = {}, messageId = {}", dialogId, messageId);
        String deleteMessage = "DELETE FROM message WHERE id = ? AND dialog_id = ?";
        jdbcTemplate.update(deleteMessage, messageId, dialogId);
        log.info("deleteById(): finish():");
    }

    public Integer getCountUnreadedMessagesForUser(int id) {

        log.info("getCountUnreadedMessagesForUser(): start():");
        log.debug("getCountUnreadedMessagesForUser(): id = {}", id);
        String getUnreaded = "SELECT count(*) FROM message WHERE recipient_id = ? AND read_status = 'SENT'";
        Integer count = jdbcTemplate.queryForObject(getUnreaded, new Object[]{id}, Integer.class);
        log.debug("getCountUnreadedMessagesForUser(): count = {}", count);
        log.info("getCountUnreadedMessagesForUser(): start():");
        return count;
    }

    public Integer getCountUnreadedMessagesInDialog(int activeUserId, int dialogId) {

        log.info("getCountUnreadedMessagesInDialog(): start():");
        log.debug("getCountUnreadedMessagesInDialog(): activeUserId = {}, dialogId = {}", activeUserId, dialogId);
        String getUnreaded = "SELECT count(*) FROM message WHERE (recipient_id = ? AND dialog_id = ?) " +
                "AND read_status = 'SENT'";
        Integer count = jdbcTemplate.queryForObject(getUnreaded, new Object[]{activeUserId, dialogId}, Integer.class);
        log.debug("getCountUnreadedMessagesInDialog(): count = {}", count);
        log.info("getCountUnreadedMessagesInDialog(): finish():");
        return count;
    }

    public Message getLastMessageInDialog(Dialog dialog) {

        log.info("getLastMessageInDialog(): start():");
        log.debug("getLastMessageInDialog(): dialog = {}", dialog);
        String getLastMessage = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time DESC LIMIT 1";
        Message message = jdbcTemplate.queryForObject(getLastMessage, new Object[]{dialog.getFirstUserId(),
                        dialog.getSecondUserId(), dialog.getSecondUserId(), dialog.getFirstUserId()},
                new MessageMapper());
        log.debug("getLastMessageInDialog(): message = {}", message);
        log.info("getLastMessageInDialog(): finish():");
        return message;
    }

    public List<Message> getMessagesInDialog(Dialog dialog) {

        log.info("getMessagesInDialog(): start():");
        log.debug("getMessagesInDialog(): dialog = {}", dialog);
        String getMessages = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time";
        List<Message> messages = jdbcTemplate.query(getMessages, new Object[]{dialog.getFirstUserId(), dialog.getSecondUserId(),
                        dialog.getSecondUserId(), dialog.getFirstUserId()},
                new MessageMapper());
        log.debug("getMessagesInDialog(): messages = {}", messages);
        log.info("getMessagesInDialog(): finish():");
        return messages;
    }
}
