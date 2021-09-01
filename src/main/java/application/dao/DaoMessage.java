package application.dao;

import application.dao.mappers.DialogMapper;
import application.dao.mappers.MessageMapper;
import application.models.Dialog;
import application.models.Message;
import application.models.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional
public class DaoMessage implements Dao<Message> {

    private final JdbcTemplate jdbcTemplate;
    private final DaoNotification daoNotification;
    private final DaoPerson daoPerson;

    @Override
    public Message getById(int id) {
        String selectMessageById = "SELECT * FROM message WHERE id = ?";
        return jdbcTemplate.query(selectMessageById, new Object[]{id}, new MessageMapper()).stream()
                .findAny().orElse(null);
    }

    @Override
    public List<Message> getAll() {
        String selectAll = "SELECT * FROM message";
        return jdbcTemplate.query(selectAll, new MessageMapper());
    }

    @Override
    public void save(Message message) {

    }

    @Override
    public void update(Message message) {

    }

    public Message saveAndReturnMessage(Message message) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("message")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", System.currentTimeMillis());
        parameters.put("author_id", message.getAuthorId());
        parameters.put("recipient_id", message.getRecipientId());
        parameters.put("message_text", message.getMessageText());
        parameters.put("read_status", message.getReadStatus().toString());
        parameters.put("dialog_id", message.getDialogId());
        Message insertedMessage = getById(sji.executeAndReturnKey(parameters).intValue());
        daoNotification.addNotification(message.getRecipientId(), daoPerson.getAuthPerson().getId(), insertedMessage.getTime(), message.getId(),
                daoPerson.getById(message.getAuthorId()).getEmail(), NotificationType.MESSAGE.toString(), message.getMessageText());
        return insertedMessage;
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

    @Override
    public void delete(int messageId) {

    }

    public void deleteById(int messageId, int dialogId) {
        String deleteMessage = "DELETE FROM message WHERE id = ? AND dialog_id = ?";
        jdbcTemplate.update(deleteMessage, messageId, dialogId);
    }

    public Dialog getDialogById(int id) {
        String selectDialog = "SELECT * FROM dialog WHERE id = ?";
        return jdbcTemplate.query(selectDialog, new Object[]{id}, new DialogMapper()).stream().findAny().orElse(null);
    }

    public Integer getCountUnreadedMessagesForUser(int id) {
        String getUnreaded = "SELECT count(*) FROM message WHERE recipient_id = ? AND read_status = 'SENT'";
        return jdbcTemplate.queryForObject(getUnreaded, new Object[]{id}, Integer.class);
    }

    public List<Dialog> getDialogListForUser(int id) {
        String getDialogs = "SELECT * FROM dialog WHERE first_user_id = ? OR second_user_id = ?";
        return jdbcTemplate.query(getDialogs, new Object[]{id, id}, new DialogMapper());
    }

    public Integer getCountUnreadedMessagesInDialog(int activeUserId, int dialogId) {
        String getUnreaded = "SELECT count(*) FROM message WHERE (recipient_id = ? AND dialog_id = ?) " +
                "AND read_status = 'SENT'";
        return jdbcTemplate.queryForObject(getUnreaded, new Object[]{activeUserId, dialogId}, Integer.class);
    }

    public Message getLastMessageInDialog(Dialog dialog) {
        String getLastMessage = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time DESC LIMIT 1";
        return jdbcTemplate.queryForObject(getLastMessage, new Object[]{
                dialog.getFirstUserId(),
                dialog.getSecondUserId(),
                dialog.getSecondUserId(),
                dialog.getFirstUserId()},
                new MessageMapper());
    }

    public List<Message> getMessagesInDialog(int dialogId) {
        String getMessages = "SELECT * FROM message WHERE ((recipient_id = ? AND author_id = ?) OR " +
                "(recipient_id = ? AND author_id = ?)) ORDER BY time";
        Dialog dialog = getDialogById(dialogId);
        return jdbcTemplate.query(getMessages, new Object[]{
                dialog.getFirstUserId(),
                dialog.getSecondUserId(),
                dialog.getSecondUserId(),
                dialog.getFirstUserId()},
                new MessageMapper());
    }

    public int createDialog(int firstUserId, int secondUserId) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("dialog")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("first_user_id", firstUserId);
        parameters.put("second_user_id", secondUserId);
        return sji.executeAndReturnKey(parameters).intValue();
    }

    public Dialog getDialogByUsersId(int firstUserId, int secondUserId) {
        String getDialog = "SELECT * FROM dialog WHERE (first_user_id = ? AND second_user_id = ?) OR " +
                "(first_user_id = ? AND second_user_id = ?)";
        return jdbcTemplate.query(getDialog, new Object[]{firstUserId, secondUserId, secondUserId, firstUserId},
                new DialogMapper()).stream().findAny().orElse(null);
    }
}
