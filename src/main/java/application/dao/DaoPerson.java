package application.dao;

import application.models.PermissionMessagesType;
import application.models.Person;
import application.models.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoPerson implements Dao<Person> {

    private final JdbcTemplate jdbcTemplate;

    private final static String SQL_INSERT_PERSON = "INSERT INTO person (first_name, last_name, password, e_mail, reg_date, messages_permission) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_FIND_PERSON_BY_EMAIL = "SELECT * FROM person WHERE e_mail = ?";
    private final static String SQL_FIND_PERSON_BY_ID = "SELECT * FROM person WHERE id = ?";
    private final static String SQL_SELECT_RECOMMENDATIONS = "SELECT * FROM person";

    public Person getByEmail(String email) {
        return jdbcTemplate.query(SQL_FIND_PERSON_BY_EMAIL, new Object[]{email}, new PersonMapper()).stream().findAny()
                .orElse(null);
    }

    @Override
    public Person get(int id) {
        return jdbcTemplate.query(SQL_FIND_PERSON_BY_ID, new Object[]{id}, new PersonMapper()).stream().findAny().orElse(null);
    }

    @Override
    public List<Person> getAll() {
        return null;
    }

    public List<Person> getRecommendations() {

        return jdbcTemplate.query(SQL_SELECT_RECOMMENDATIONS,
                new PersonMapper());
    }

    public void save(Person person) {
        jdbcTemplate.update(SQL_INSERT_PERSON,
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getEmail(),
                System.currentTimeMillis(),
                PermissionMessagesType.ALL.toString());
    }

    @Override
    public void update(Person person, String... params) {

    }

    @Override
    public void delete(Person person) {

    }

    public List<Person> getFriends(int id) {
        String selectFriends = "SELECT * FROM person JOIN friendship ON person.id = friendship.dst_person_id" +
                " JOIN friendship_status ON friendship_status.id = friendship.status_id WHERE code = 'FRIEND' AND person.id !=" + id +
                " UNION SELECT * FROM person JOIN friendship ON person.id = friendship.src_person_id" +
                " JOIN friendship_status ON friendship_status.id = friendship.status_id WHERE code = 'FRIEND' AND person.id !=" + id;
        return jdbcTemplate.query(selectFriends, new PersonMapper());
    }
}
