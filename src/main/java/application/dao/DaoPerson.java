package application.dao;

import application.dao.mappers.PersonMapper;
import application.models.FriendshipStatus;
import application.models.PermissionMessagesType;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DaoPerson {

    private final JdbcTemplate jdbcTemplate;

    public Integer getPersonIdByEmail(String email) {

        String selectPersonIdByEmail = "SELECT id FROM person WHERE e_mail = ?";
        return jdbcTemplate.queryForObject(selectPersonIdByEmail, new Object[]{email}, Integer.class);
    }

    public Person getByEmail(String email) {

        String selectPersonByEmail = "SELECT * FROM person WHERE e_mail = ?";
        return jdbcTemplate.query(selectPersonByEmail, new Object[]{email}, new PersonMapper()).stream().findAny()
                .orElse(null);
    }

    public Person getByConfirmationCode(String code) {

        String query = "SELECT * FROM person WHERE confirmation_code = ?";
        return jdbcTemplate.query(query, new Object[]{code}, new PersonMapper()).stream().findAny().orElse(null);
    }

    public Person getById(int id) {

        String selectPersonForId = "SELECT * FROM person WHERE id = ?";
        return jdbcTemplate.query(selectPersonForId, new Object[]{id}, new PersonMapper()).stream().findAny()
                .orElse(null);
    }

    public List<Person> getRecommendations(int id) {

        String selectRecommendations = "SELECT * FROM person WHERE id IN (SELECT src_person_id from friendship " +
                "JOIN friendship_status fs on fs.id = friendship.status_id WHERE code = ? AND dst_person_id " +
                "IN (SELECT src_person_id FROM friendship WHERE dst_person_id = ?)) AND person.id != ? " +
                "UNION SELECT * FROM person WHERE id IN (SELECT dst_person_id from friendship " +
                "JOIN friendship_status f on f.id = friendship.status_id WHERE code = ? AND src_person_id " +
                "IN (SELECT dst_person_id FROM friendship WHERE src_person_id = ?)) AND id != ?";
        return jdbcTemplate.query(selectRecommendations, new Object[]{FriendshipStatus.FRIEND.toString(), id, id,
                        FriendshipStatus.FRIEND.toString(), id, id},
                new PersonMapper());
    }

    public void save(Person person) {

        String sqlInsertPerson = "INSERT INTO person (first_name, last_name, password," +
                " e_mail, reg_date, messages_permission, photo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertPerson, person.getFirstName(), person.getLastName(), person.getPassword(),
                person.getEmail(), System.currentTimeMillis(), PermissionMessagesType.ALL.toString(), person.getPhoto());
    }

    public void updatePersonData(int id, String firstName,String lastName, long birthDate, String phone, String photo,
                                 String city, String country, String about){

        jdbcTemplate.update("UPDATE person SET first_name = ?, last_name = ?," +
        "birth_date = ?, phone = ?, photo = ?, city = ?, country = ?, about = ? WHERE id = ?", firstName, lastName,
                birthDate, phone, photo, city, country, about, id);
    }

    public void updateConfirmationCode(int id, String code) {

        String query = "UPDATE person SET confirmation_code = ? WHERE id = ?";
        jdbcTemplate.update(query, code, id);
    }

    public void updatePassword(int id, String password) {

        String query = "UPDATE person SET password = ? WHERE id = ?";
        jdbcTemplate.update(query, password, id);
    }

    public void delete(int id) {

        jdbcTemplate.update("DELETE FROM person where id = ?", id);
    }

    public void deleteFriendshipByPersonId(int id){

        jdbcTemplate.update("DELETE FROM friendship_status WHERE id = (SELECT status_id FROM friendship " +
                "WHERE src_person_id = ?)", id);
        jdbcTemplate.update("DELETE FROM friendship_status WHERE id = (SELECT status_id FROM friendship " +
                "WHERE dst_person_id = ?)", id);
        jdbcTemplate.update("DELETE FROM friendship WHERE src_person_id = ?", id);
        jdbcTemplate.update("DELETE FROM friendship WHERE dst_person_id = ?", id);
    }

    public List<Person> getFriends(int id) {

        String select = "SELECT * FROM person WHERE id IN (SELECT src_person_id FROM friendship" +
                " JOIN friendship_status fs on fs.id = friendship.status_id " +
                "WHERE code = '" + FriendshipStatus.FRIEND + "' AND dst_person_id = " + id + " " +
                "UNION SELECT dst_person_id FROM friendship JOIN friendship_status fs " +
                "ON fs.id = friendship.status_id WHERE code = '" + FriendshipStatus.FRIEND + "' " +
                "AND src_person_id = " + id + ")";

        return jdbcTemplate.query(select, new PersonMapper());
    }

    @Transactional
    public Integer addFriendByIdAndReturnEntityId(int srcId, int dstId) {

        Map<String, Object> params = new HashMap<>();
        params.put("time", System.currentTimeMillis());
        params.put("name", "Запрос на добавление в друзья");
        params.put("code", FriendshipStatus.REQUEST);

        int friendshipStatusId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship_status").usingGeneratedKeyColumns("id")
                .executeAndReturnKey(params).intValue();
        params.clear();

        params.put("status_id", friendshipStatusId);
        params.put("src_person_id", srcId);
        params.put("dst_person_id", dstId);

        return new SimpleJdbcInsert(jdbcTemplate).withTableName("friendship").usingGeneratedKeyColumns("id")
                .executeAndReturnKey(params).intValue();
    }

    public void addFriendRequest(int srcId, int dstId) {

        String update = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id FROM friendship" +
                " WHERE src_person_id = ? AND dst_person_id = ?)";

        jdbcTemplate.update(update, FriendshipStatus.FRIEND.toString(), srcId, dstId);
    }

    public List<Person> getFriendsRequest(int id) {

        String selectRequests = "SELECT * FROM  person WHERE id IN (SELECT src_person_id FROM friendship " +
                "JOIN friendship_status fs on fs.id = friendship.status_id WHERE code = ? AND dst_person_id = ?)";

        return jdbcTemplate.query(selectRequests, new Object[]{FriendshipStatus.REQUEST.toString(),
                id}, new PersonMapper());
    }

    public String getFriendStatus(int srcId, int dstId) {

        String select = "SELECT code FROM friendship f JOIN friendship_status fs2 ON f.status_id = fs2.id WHERE" +
                " src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        return jdbcTemplate.queryForObject(select, new Object[]{srcId, dstId, dstId, srcId}, String.class);
    }

    public void deleteFriendForID(int srcId, int dtcId) {

        String selectStatusId = "SELECT status_id FROM friendship WHERE src_person_id IN (?, ?) " +
                "AND dst_person_id IN (?, ?)";
        String deleteFriendshipStatus = "DELETE from friendship_status WHERE id = ?";
        String deleteFriendship = "DELETE FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        Integer selectedId = jdbcTemplate.queryForObject(selectStatusId, new Object[]{srcId, dtcId, srcId, dtcId},
                    Integer.class);
        jdbcTemplate.update(deleteFriendship, srcId, dtcId, dtcId, srcId);
        jdbcTemplate.update(deleteFriendshipStatus, selectedId);
    }

    public Person getAuthPerson() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getByEmail(authentication.getName());
    }

    public void unAcceptRequest(int dstId, int srcId) {

        String updateFriendshipStatus = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id " +
                "FROM friendship WHERE dst_person_id = ? AND src_person_id = ?)";
        jdbcTemplate.update(updateFriendshipStatus, FriendshipStatus.DECLINED.toString(), dstId, srcId);
    }

    @Transactional
    public void updateDeclined(int srcId, int dtcId) {

        String updateFriendship = "UPDATE friendship SET src_person_id = ?, dst_person_id = ? " +
                "WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        String updateFriendshipStatus = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id " +
                "FROM friendship WHERE src_person_id = ? AND dst_person_id = ?)";
        jdbcTemplate.update(updateFriendship, srcId, dtcId, srcId, dtcId, dtcId, srcId);
        jdbcTemplate.update(updateFriendshipStatus, FriendshipStatus.REQUEST, srcId, dtcId);
    }

    public List<Person> getRecommendationsOnRegDate(int id) {

        String selectRecommendations = "SELECT * FROM person WHERE reg_date > ? AND id != ?";
        long twoDays = 17280000000L;
        return jdbcTemplate.query(selectRecommendations, new Object[]{System.currentTimeMillis() - twoDays, id},
                new PersonMapper());
    }

    public List<Person> getPersons(String firstName, String lastName, Long ageFrom, Long ageTo, String country,
                                   String city) {

        String query = "SELECT * FROM person WHERE (first_name ILIKE ? OR ?::text IS NULL)" +
                "AND (last_name ILIKE ? OR ?::text IS NULL) AND (birth_date >= ? OR ?::bigint IS NULL) " +
                "AND (birth_date <= ? OR ?::bigint IS NULL) AND (country ILIKE ? OR ?::text IS NULL) " +
                "AND (city ILIKE ? OR ?::text IS NULL)";

        return new ArrayList<>(jdbcTemplate.query(query, new Object[]{prepareParam(firstName), firstName,
                prepareParam(lastName), lastName, ageFrom, ageFrom, ageTo, ageTo, prepareParam(country), country,
                prepareParam(city), city}, new PersonMapper()));
    }

    private String prepareParam(String param) {
        return "%" + param + "%";
    }

    public List<Person> getPersonsByFirstNameSurname(String firstOrLastName) {

        String query = "SELECT * FROM person WHERE (first_name ILIKE ?) " +
                "OR (last_name ILIKE ?)";

        return jdbcTemplate.query(query, new Object[]{prepareParam(firstOrLastName), prepareParam(firstOrLastName)},
                new PersonMapper());
    }

    public void updateEmail(int id, String email) {

        String query = "UPDATE person SET e_mail = ? WHERE id = ?";
        jdbcTemplate.update(query, email, id);
    }

    public Long getLastOnlineTime(int id) {

        String query = "SELECT last_online_time FROM person WHERE id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, Long.class);
    }
}
