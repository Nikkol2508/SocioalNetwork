package application.dao;

import application.dao.mappers.DialogMapper;
import application.models.Dialog;
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
public class DaoDialog {

    private final JdbcTemplate jdbcTemplate;

    public Dialog getDialogById(int id) {

        log.info("getDialogById(): start():");
        log.debug("getDialogById(): id = {}", id);
        String selectDialog = "SELECT * FROM dialog WHERE id = ?";
        Dialog dialog = jdbcTemplate.query(selectDialog, new Object[]{id}, new DialogMapper()).stream().findAny()
                .orElse(null);
        log.debug("getDialogById(): dialog = {}", dialog);
        log.info("getDialogById(): finish():");
        return dialog;
    }

    public List<Dialog> getDialogListForUser(int id) {

        log.info("getDialogListForUser(): start():");
        log.debug("getDialogListForUser(): id = {}", id);
        String getDialogs = "SELECT * FROM dialog WHERE first_user_id = ? OR second_user_id = ?";
        List<Dialog> dialogList = jdbcTemplate.query(getDialogs, new Object[]{id, id}, new DialogMapper());
        log.debug("getDialogListForUser(): dialogList = {}", dialogList);
        log.info("getDialogListForUser(): finish():");
        return dialogList;
    }

    public int createDialog(int firstUserId, int secondUserId) {

        log.info("createDialog(): start():");
        log.debug("createDialog(): firstUserId = {}, secondUserId = {}", firstUserId, secondUserId);
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("dialog")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("first_user_id", firstUserId);
        parameters.put("second_user_id", secondUserId);
        int key = sji.executeAndReturnKey(parameters).intValue();
        log.debug("createDialog(): key = {}", key);
        log.info("createDialog(): finish():");
        return key;
    }

    public Dialog getDialogByUsersId(int firstUserId, int secondUserId) {
        log.info("getDialogByUsersId(): start():");
        log.debug("getDialogByUsersId(): firstUserId = {}, secondUserId = {}", firstUserId, secondUserId);
        String getDialog = "SELECT * FROM dialog WHERE (first_user_id = ? AND second_user_id = ?) OR " +
                "(first_user_id = ? AND second_user_id = ?)";
        Dialog dialog = jdbcTemplate.query(getDialog, new Object[]{firstUserId, secondUserId, secondUserId, firstUserId},
                new DialogMapper()).stream().findAny().orElse(null);
        log.debug("getDialogByUsersId(): dialog = {}", dialog);
        log.info("getDialogByUsersId(): finish():");
        return dialog;
    }

    public void deleteDialogById(int id) {
        log.info("deleteDialogById(): start():");
        log.debug("deleteDialogById(): id = {}", id);
        String deleteDialog = "DELETE FROM dialog WHERE id = ?";
        jdbcTemplate.update(deleteDialog, id);
        log.info("deleteDialogById(): finish():");
    }
}
