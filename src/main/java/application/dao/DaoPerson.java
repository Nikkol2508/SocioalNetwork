package application.dao;

import application.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DaoPerson implements Dao<Person> {

    private final JdbcTemplate jdbcTemplate;

    private final static String SQL_INSERT_PERSON = "INSERT INTO person (" +
            "first_name, last_name, password, e_mail, reg_date, messages_permission) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_FIND_PERSON_BY_EMAIL = "SELECT * FROM person WHERE e_mail = ?";
    private final static String SQL_FIND_PERSON_BY_ID = "SELECT * FROM person WHERE id = ?";
    private final static String SQL_SELECT_RECOMMENDATIONS = "SELECT * FROM person";
    private final static String SQL_FIND_PERSON_BY_CONFIRMATION_CODE = "SELECT * FROM person WHERE confirmation_code = ?";
    private final static String SQL_UPDATE_CONFIRMATION_CODE = "UPDATE person SET confirmation_code = ? WHERE id = ?";
    private final static String SQL_UPDATE_PASSWORD = "UPDATE person SET password = ? WHERE id = ?";
    private final static String SQL_UPDATE_EMAIL = "UPDATE person SET e_mail = ? WHERE id = ?";

    public Person getByEmail(String email) {
        return jdbcTemplate.query(SQL_FIND_PERSON_BY_EMAIL, new Object[]{email}, new PersonMapper()).stream().findAny()
                .orElse(null);
    }

    public Person getByConfirmationCode(String code) {
        return jdbcTemplate.query(SQL_FIND_PERSON_BY_CONFIRMATION_CODE, new Object[]{code}, new PersonMapper())
                .stream().findAny().orElse(null);
    }

    @Override
    public Person get(int id) {
        return jdbcTemplate.query(SQL_FIND_PERSON_BY_ID, new Object[]{id}, new PersonMapper())
                .stream().findAny().orElse(null);
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
                person.getMessagesPermission().toString());
    }

    @Override
    public void update(Person person) {

    }

    public void updateConfirmationCode(int id, String code) {
        jdbcTemplate.update(SQL_UPDATE_CONFIRMATION_CODE, code, id);
    }

    public void updatePassword(int id, String password) {
        jdbcTemplate.update(SQL_UPDATE_PASSWORD, password, id);
    }

    public void updateEmail(int id, String email) {
        jdbcTemplate.update(SQL_UPDATE_EMAIL, email, id);
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

    public List<Person> getPersons(String firstName, String lastName, Long ageFrom, Long ageTo, String country, String city) {

        String query = "select * from person where " +
                "(first_name = ? or ?::text IS NULL) " +
                "and (last_name  = ? or ?::text is null) " +
                "and (birth_date >= ? or ?::bigint is null) " +
                "and (birth_date <= ? or ?::bigint is null) " +
                "and (country = ? or ?::text is null) " +
                "and (city = ? or ?::text is null)";

        return new ArrayList<>(jdbcTemplate.query(query,
                new Object[]{firstName, firstName,
                        lastName, lastName,
                        ageFrom, ageFrom,
                        ageTo, ageTo,
                        country, country,
                        city, city},
                new PersonMapper()));

    }
}
