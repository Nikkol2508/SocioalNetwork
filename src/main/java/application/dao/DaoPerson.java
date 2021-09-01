package application.dao;

import application.dao.mappers.IdMapper;
import application.dao.mappers.PersonMapper;
import application.models.FriendshipStatus;
import application.models.NotificationType;
import application.models.PermissionMessagesType;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DaoPerson implements Dao<Person> {

    private final JdbcTemplate jdbcTemplate;
    private final DaoNotification daoNotification;

    public Integer getPersonIdByEmail(String email) {
        String selectPersonIdByEmail = "SELECT id FROM person WHERE e_mail = ?";

        return jdbcTemplate.query(selectPersonIdByEmail, new Object[]{email}, new IdMapper())
            .stream().findAny().orElse(null);
    }

    public Person getByEmail(String email) {
        String selectPersonByEmail = "SELECT * FROM person WHERE e_mail = ?";
        return jdbcTemplate.query(selectPersonByEmail, new Object[]{email}, new PersonMapper()).stream().findAny()
                .orElse(null);
    }

    public Person getByConfirmationCode(String code) {

        String query = "SELECT * FROM person WHERE confirmation_code = ?";
        return jdbcTemplate.query(query, new Object[]{code}, new PersonMapper())
                .stream().findAny().orElse(null);
    }

    @Override
    public Person getById(int id) {
        String selectPersonForId = "SELECT * FROM person WHERE id = ?";
        return jdbcTemplate.query(selectPersonForId, new Object[]{id}, new PersonMapper()).stream()
                .findAny().orElse(null);
    }

    @Override
    public List<Person> getAll() {
        return null;
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
                " e_mail, reg_date, messages_permission, photo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertPerson,
                person.getFirstName(),
                person.getLastName(),
                person.getPassword(),
                person.getEmail(),
                System.currentTimeMillis(),
                PermissionMessagesType.ALL.toString(),
                person.getPhoto());
    }

    @Override
    public void update(Person person) {
    }

    public void updatePersonData(int id, String firstName,String lastName, long birthDate, String phone, String photo,
                                 String city, String country, String about){
        jdbcTemplate.update("UPDATE person SET first_name = ?, last_name = ?," +
        "birth_date = ?, phone = ?, photo = ?, city = ?, country = ?, about = ? WHERE id = ?",
                firstName, lastName, birthDate, phone, photo,
                city, country, about, id);
    }

    public void updateConfirmationCode(int id, String code) {

        String query = "UPDATE person SET confirmation_code = ? WHERE id = ?";
        jdbcTemplate.update(query, code, id);
    }

    public void updatePassword(int id, String password) {

        String query = "UPDATE person SET password = ? WHERE id = ?";
        jdbcTemplate.update(query, password, id);
    }

    @Override
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
                "WHERE code = '" + FriendshipStatus.FRIEND + "' AND dst_person_id = " + id + " union SELECT dst_person_id" +
                " FROM friendship JOIN friendship_status fs " +
                "on fs.id = friendship.status_id WHERE code = '" + FriendshipStatus.FRIEND + "' AND src_person_id = " + id + ")";

        return jdbcTemplate.query(select, new PersonMapper());
    }

    public void addFriendForId(int srcId, int dtsId) {

        String insertIntoFriendshipStatus = "INSERT INTO friendship_status (time, name, code) VALUES (?, ?, ?)";
        String insetIntoFriendship = "INSERT INTO friendship (status_id, src_person_id, dst_person_id) " +
                "VALUES ((SELECT max(friendship_status.id) from friendship_status), ?, ?)";

        jdbcTemplate.update(insertIntoFriendshipStatus, System.currentTimeMillis(),
                "Запрос на добавление в друзья",
                FriendshipStatus.REQUEST.toString());

        jdbcTemplate.update(insetIntoFriendship, srcId, dtsId);
        int entityId = jdbcTemplate.queryForObject("SELECT status_id FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id" +
                " IN (?, ?)", new Object[]{srcId, dtsId, dtsId, srcId}, Integer.class);
        daoNotification.addNotification(dtsId, srcId, System.currentTimeMillis(), entityId,
                getById(dtsId).getEmail(), NotificationType.FRIEND_REQUEST.toString());
    }

    public void addFriendRequest(int srcId, int dtsId) {

        String update = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id FROM friendship" +
                " WHERE src_person_id = ? AND dst_person_id = ?)";

        jdbcTemplate.update(update, FriendshipStatus.FRIEND.toString(), srcId, dtsId);
    }

    public List<Person> getFriendsRequest(int id) {

        String selectRequests = "SELECT * FROM  person WHERE id IN (SELECT src_person_id FROM friendship " +
                "JOIN friendship_status fs on fs.id = friendship.status_id WHERE code = ? AND dst_person_id = ?)";

        return jdbcTemplate.query(selectRequests, new Object[]{FriendshipStatus.REQUEST.toString(),
                id}, new PersonMapper());
    }

    public String getFriendStatus(int srcId, int dtsId) {

        try {
            String select = "select code from friendship f join friendship_status fs2 on f.status_id = fs2.id " +
                    "where src_person_id IN (?, ?)" +
                    " and dst_person_id IN (?, ?)";

            return jdbcTemplate.queryForObject(select, new Object[]{srcId, dtsId, dtsId, srcId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteFriendForID(int srcId, int dtcId) {
        String selectStatusId = "SELECT status_id FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";

        String deleteFriendshipStatus = "DELETE from friendship_status WHERE id = ?";
        int selectedId = jdbcTemplate.queryForObject(selectStatusId, new Object[]{srcId, dtcId, srcId, dtcId}, Integer.class);

        String deleteFriendship = "DELETE FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";

        jdbcTemplate.update(deleteFriendship, srcId, dtcId, dtcId, srcId);
        jdbcTemplate.update(deleteFriendshipStatus, selectedId);
    }

    public Person getAuthPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getByEmail(authentication.getName());
    }

    public void unAcceptRequest(int dtsId, int srcId) {
        String updateFriendshipStatus = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id " +
                "FROM friendship WHERE dst_person_id = ? AND src_person_id = ?)";
        jdbcTemplate.update(updateFriendshipStatus, FriendshipStatus.DECLINED.toString(),
                dtsId, srcId);
    }

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

    public List<Person> getPersonsByFirstNameSurname(String author) {

        String query = "select * from person where " +
                "(first_name = ? or ?::text IS NULL) " +
                "or (last_name  = ? or ?::text is null)";

        return jdbcTemplate.query(query,
                new Object[]{author, author,
                        author, author},
                new PersonMapper());
    }

    public void updateEmail(int id, String email) {
        String query = "UPDATE person SET e_mail = ? WHERE id = ?";
        jdbcTemplate.update(query, email, id);
    }

    public Long getLastOnlineTime(int id) {
        String query = "SELECT last_online_time FROM person WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{id}, Long.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void updateLastOnlineTime(int id) {
        String query = "UPDATE person SET last_online_time = ? WHERE id = ?";
        jdbcTemplate.update(query, System.currentTimeMillis(), id);
    }
}
