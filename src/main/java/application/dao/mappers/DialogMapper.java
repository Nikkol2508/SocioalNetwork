package application.dao.mappers;

import application.models.Dialog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DialogMapper implements RowMapper<Dialog> {

    @Override
    public Dialog mapRow(ResultSet rs, int rowNum) throws SQLException {
        Dialog dialog = new Dialog();
        dialog.setId(rs.getInt("id"));
        dialog.setFirstUserId(rs.getInt("first_user_id"));
        dialog.setSecondUserId(rs.getInt("second_user_id"));
        return dialog;
    }
}
