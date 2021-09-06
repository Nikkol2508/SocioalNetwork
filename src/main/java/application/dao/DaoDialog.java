package application.dao;

import application.dao.mappers.DialogMapper;
import application.models.Dialog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DaoDialog {

    private final JdbcTemplate jdbcTemplate;

    public Dialog getDialogById(int id) {

        String selectDialog = "SELECT * FROM dialog WHERE id = ?";
        return jdbcTemplate.query(selectDialog, new Object[]{id}, new DialogMapper()).stream().findAny()
                .orElse(null);
    }

    public List<Dialog> getDialogListForUser(int id) {

        String getDialogs = "SELECT * FROM dialog WHERE first_user_id = ? OR second_user_id = ?";
        return jdbcTemplate.query(getDialogs, new Object[]{id, id}, new DialogMapper());
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

    public void deleteDialogById(int id) {

        String deleteDialog = "DELETE FROM dialog WHERE id = ?";
        jdbcTemplate.update(deleteDialog, id);
    }
}
