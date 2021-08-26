package application.dao;

import application.dao.mappers.DialogMapper;
import application.dao.mappers.MessageMapper;
import application.models.Dialog;
import application.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DaoMessage implements Dao<Message> {

    private final JdbcTemplate jdbcTemplate;

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
        String insertMessage = "INSERT INTO message (time, author_id, " +
                "recipient_id, message_text, read_status, dialog_id) VALUES (?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, System.currentTimeMillis());
            ps.setInt(2, message.getAuthorId());
            ps.setInt(3, message.getRecipientId());
            ps.setString(4, message.getMessageText());
            ps.setString(5, message.getReadStatus().toString());
            ps.setInt(6, message.getDialogId());
            return ps;
        }, keyHolder);
        return getById((int) keyHolder.getKeys().get("id"));
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
    public void delete(Message message) {

    }

    public void deleteById(int messageId, int dialogId) {
        String deleteMessage = "DELETE FROM message WHERE id = ? AND dialog_id = ?";
        jdbcTemplate.update(deleteMessage, messageId, dialogId);
    }

    public Dialog getDialogById(int id) {
        String selectDialog = "SELECT * FROM dialog WHERE id = ?";
        return jdbcTemplate.query(selectDialog, new Object[]{id}, new DialogMapper()).stream().findAny().orElse(null);
    }
}
