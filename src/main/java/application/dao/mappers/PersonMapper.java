package application.dao.mappers;

import application.models.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setRegDate(rs.getLong("reg_date"));
        person.setBirthDate(rs.getLong("birth_date"));
        person.setEmail(rs.getString("e_mail"));
        person.setPhone(rs.getString("phone"));
        person.setPassword(rs.getString("password"));
        person.setPhoto(rs.getString("photo"));
        person.setAbout(rs.getString("about"));
        person.setCity(rs.getString("city"));
        person.setCountry(rs.getString("country"));
        person.setConfirmationCode(rs.getString("confirmation_code"));
        person.setApproved(rs.getBoolean("is_approved"));
        person.setLastOnlineTime(rs.getLong("last_online_time"));
        person.setMessagesPermission(rs.getString("messages_permission"));
        person.setBlocked(rs.getBoolean("is_blocked"));
        return person;
    }
}

