package application.dao;

import application.dao.mappers.MessageMapper;
import application.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void delete(Message message) {

    }
}
