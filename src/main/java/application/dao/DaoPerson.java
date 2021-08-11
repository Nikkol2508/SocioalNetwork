package application.dao;

import application.exceptions.SetPasswordException;
import application.models.PermissionMessagesType;
import application.models.Person;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DaoPerson implements Dao<Person> {

  private final JdbcTemplate jdbcTemplate;

  private final static String SQL_INSERT_PERSON =
      "INSERT INTO person (first_name, last_name, password, e_mail, reg_date, messages_permission) "
          +
          "VALUES (?, ?, ?, ?, ?, ?)";
  private final static String SQL_FIND_PERSON_BY_EMAIL = "SELECT * FROM person WHERE e_mail = ?";
  private final static String SQL_FIND_PERSON_BY_ID = "SELECT * FROM person WHERE id = ?";
  private final static String SQL_SELECT_RECOMMENDATIONS = "SELECT * FROM person";
  private final static String SQL_SET_PASSWORD_BY_EMAIL =
      "UPDATE person SET password = ? WHERE e_mail = ?";

  public Person getByEmail(String email) {
    return jdbcTemplate.query(SQL_FIND_PERSON_BY_EMAIL, new Object[]{email}, new PersonMapper())
        .stream().findAny()
        .orElse(null);
  }

  @Override
  public Person get(int id) {
    return jdbcTemplate.query(SQL_FIND_PERSON_BY_ID, new Object[]{id}, new PersonMapper()).stream()
        .findAny().orElse(null);
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
    String selectFriends =
        "SELECT * FROM person JOIN friendship ON person.id = friendship.dst_person_id" +
            " JOIN friendship_status ON friendship_status.id = friendship.status_id WHERE code = 'FRIEND' AND person.id !="
            + id +
            " UNION SELECT * FROM person JOIN friendship ON person.id = friendship.src_person_id" +
            " JOIN friendship_status ON friendship_status.id = friendship.status_id WHERE code = 'FRIEND' AND person.id !="
            + id;
    return jdbcTemplate.query(selectFriends, new PersonMapper());
  }

  public List<Person> getPersons(String firstName, String lastName, Long ageFrom, Long ageTo,
      String country, String city) {

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

  @Transactional(rollbackFor = {SetPasswordException.class})
  public boolean setPassword(String email, String password) throws SetPasswordException {

    boolean passwordIsChanged = false;
    int updatedRawCount = jdbcTemplate.update(SQL_SET_PASSWORD_BY_EMAIL, password, email);
    if (updatedRawCount > 1) {

      throw new SetPasswordException();
    } else if (updatedRawCount == 0) {
      return false;
    } else {
      return true;
    }
  }
}
