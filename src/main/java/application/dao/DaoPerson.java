package application.dao;

import application.dao.mappers.PersonMapper;
import application.models.FriendshipStatus;
import application.models.PermissionMessagesType;
import application.models.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class DaoPerson {

    private final JdbcTemplate jdbcTemplate;

    public Integer getPersonIdByEmail(String email) {

        log.info("getPersonIdByEmail(): start():");
        log.debug("getPersonIdByEmail(): email = {}", email);
        String selectPersonIdByEmail = "SELECT id FROM person WHERE e_mail = ?";
        Integer id = jdbcTemplate.queryForObject(selectPersonIdByEmail, new Object[]{email}, Integer.class);
        log.debug("getPersonIdByEmail(): id = {}", id);
        log.info("getPersonIdByEmail(): finish():");
        return id;
    }

    public Person getByEmail(String email) {

        log.info("getByEmail(): start():");
        log.debug("getByEmail(): email={}", email);
        String selectPersonByEmail = "SELECT * FROM person WHERE e_mail = ?";
        Person person = jdbcTemplate.query(selectPersonByEmail, new Object[]{email}, new PersonMapper()).stream().findAny()
                .orElse(null);
        log.debug("getByEmail(): person = {}", person);
        log.info("getByEmail(): finish():");
        return person;
    }

    public Person getByConfirmationCode(String code) {

        log.info("getByConfirmationCode(): start():");
        log.debug("getByConfirmationCode(): code = {}", code);
        String query = "SELECT * FROM person WHERE confirmation_code = ?";
        Person person = jdbcTemplate.query(query, new Object[]{code}, new PersonMapper()).stream().findAny().orElse(null);
        log.debug("getByConfirmationCode(): person = {}", person);
        log.info("getByConfirmationCode(): finish():");
        return person;
    }

    public Person getById(int id) {

        log.info("getById(): start():");
        log.debug("getById(): personId = {}", id);
        String selectPersonForId = "SELECT * FROM person WHERE id = ?";
        Person person = jdbcTemplate.query(selectPersonForId, new Object[]{id}, new PersonMapper()).stream().findAny()
                .orElse(null);
        log.debug("getById(): person = {}", person);
        log.info("getById(): start():");
        return person;
    }

    public List<Person> getRecommendations(int id) {

        log.info("getRecommendations(): start():");
        log.debug("getRecommendations(): id = {}", id);
        String selectRecommendations = "SELECT * FROM person WHERE id IN (SELECT src_person_id from friendship " +
                "JOIN friendship_status fs on fs.id = friendship.status_id WHERE code = ? AND dst_person_id " +
                "IN (SELECT src_person_id FROM friendship WHERE dst_person_id = ?)) AND person.id != ? " +
                "UNION SELECT * FROM person WHERE id IN (SELECT dst_person_id from friendship " +
                "JOIN friendship_status f on f.id = friendship.status_id WHERE code = ? AND src_person_id " +
                "IN (SELECT dst_person_id FROM friendship WHERE src_person_id = ?)) AND id != ?";
        List<Person> personList = jdbcTemplate.query(selectRecommendations, new Object[]{FriendshipStatus.FRIEND.toString(), id, id,
                        FriendshipStatus.FRIEND.toString(), id, id},
                new PersonMapper());
        log.debug("getRecommendations(): personList = {}", personList);
        log.info("getRecommendations(): finish():");
        return personList;
    }

    public List<Integer> getBlockId(int id) {
        String query = "SELECT blocked_person_id FROM blocking_persons WHERE blocking_person_id = ?";
        return jdbcTemplate.queryForList(query, new Object[]{id}, Integer.class);
    }

    public void save(Person person) {

        log.info("save(): start():");
        log.debug("save(): person = {}", person);
        String sqlInsertPerson = "INSERT INTO person (first_name, last_name, password," +
                " e_mail, reg_date, messages_permission, photo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertPerson, person.getFirstName(), person.getLastName(), person.getPassword(),
                person.getEmail(), System.currentTimeMillis(), PermissionMessagesType.ALL.toString(), person.getPhoto());
        log.info("save(): finish():");
    }

    public void updatePersonData(int id,
                                 @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$",
                                         message = "{first.name.not.valid}") String firstName,
                                 @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$",
                                         message = "{last.name.not.valid}") String lastName,
                                 Long birthDate, String phone, String photo, String city, String country, String about) {

        log.info("updatePersonData(): start():");
        log.debug("updatePersonData(): id = {}, firstName = {}, lastName = {}, birthDate = {}, " +
                "phone = {}, photo = {}, city = {}, country = {}, about = {}", id, firstName, lastName, birthDate,
                phone, photo, city, country, about);
        jdbcTemplate.update("UPDATE person SET first_name = ?, last_name = ?," +
                        "birth_date = ?, phone = ?, photo = ?, city = ?, country = ?, about = ? WHERE id = ?", firstName, lastName,
                birthDate, phone, photo, city, country, about, id);
        log.info("updatePersonData(): finish():");
    }

    public void updateConfirmationCode(int id, String code) {

        log.info("updateConfirmationCode(): start():");
        log.debug("updateConfirmationCode(): id = {}, code = {}", id, code);
        String query = "UPDATE person SET confirmation_code = ? WHERE id = ?";
        jdbcTemplate.update(query, code, id);
        log.info("updateConfirmationCode(): finish():");
    }

    public void updatePassword(int id, String password) {

        log.info("updatePassword(): start():");
        log.debug("updatePassword(): id = {}, password = {}", id, password);
        String query = "UPDATE person SET password = ? WHERE id = ?";
        jdbcTemplate.update(query, password, id);
        log.info("updatePassword(): finish():");
    }

    public void delete(int id) {

        log.info("delete(): start():");
        log.debug("delete(): id = {}", id);
        jdbcTemplate.update("DELETE FROM person where id = ?", id);
        log.info("delete(): finish():");
    }

    public void deleteFriendshipByPersonId(int id) {

        log.info("deleteFriendshipByPersonId(): start():");
        log.debug("deleteFriendshipByPersonId(): id = {}", id);
        jdbcTemplate.update("DELETE FROM friendship_status WHERE id = (SELECT status_id FROM friendship " +
                "WHERE src_person_id = ?)", id);
        jdbcTemplate.update("DELETE FROM friendship_status WHERE id = (SELECT status_id FROM friendship " +
                "WHERE dst_person_id = ?)", id);
        jdbcTemplate.update("DELETE FROM friendship WHERE src_person_id = ?", id);
        jdbcTemplate.update("DELETE FROM friendship WHERE dst_person_id = ?", id);
        log.info("deleteFriendshipByPersonId(): finish():");
    }

    public List<Person> getFriends(int id) {

        log.info("getFriends(): start():");
        log.debug("getFriends(): id = {}", id);
        String select = "SELECT * FROM person WHERE id IN (SELECT src_person_id FROM friendship" +
                " JOIN friendship_status fs on fs.id = friendship.status_id " +
                "WHERE code = '" + FriendshipStatus.FRIEND + "' AND dst_person_id = " + id + " " +
                "UNION SELECT dst_person_id FROM friendship JOIN friendship_status fs " +
                "ON fs.id = friendship.status_id WHERE code = '" + FriendshipStatus.FRIEND + "' " +
                "AND src_person_id = " + id + ")";
        List<Person> personList = jdbcTemplate.query(select, new PersonMapper());
        log.debug("getFriends(): personList = {}", personList);
        log.info("getFriends(): finish():");
        return personList;
    }

    @Transactional
    public Integer addFriendByIdAndReturnEntityId(int srcId, int dstId) {

        log.info("addFriendByIdAndReturnEntityId(): start():");
        log.debug("addFriendByIdAndReturnEntityId(): srcId = {}, dstID = {}", srcId, dstId);

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
        Integer entityId = new SimpleJdbcInsert(jdbcTemplate).withTableName("friendship").usingGeneratedKeyColumns("id")
                .executeAndReturnKey(params).intValue();
        log.debug("addFriendByIdAndReturnEntityId(): entityId = {}", entityId);
        log.info("addFriendByIdAndReturnEntityId(): finish():");
        return entityId;
    }

    public void addFriendRequest(int srcId, int dstId) {

        log.info("addFriendRequest(): start():");
        log.debug("addFriendRequest(): srcId = {}, dstId = {}", srcId, dstId);
        String update = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id FROM friendship" +
                " WHERE src_person_id = ? AND dst_person_id = ?)";

        jdbcTemplate.update(update, FriendshipStatus.FRIEND.toString(), srcId, dstId);
        log.info("addFriendRequest(): finish():");
    }

    public List<Person> getFriendsRequest(int id) {

        log.info("getFriendsRequest(): start():");
        log.debug("getFriendsRequest(): id = {}", id);
        String selectRequests = "SELECT * FROM  person WHERE id IN (SELECT src_person_id FROM friendship " +
                "JOIN friendship_status fs on fs.id = friendship.status_id WHERE code = ? AND dst_person_id = ?)";
        List<Person> personList = jdbcTemplate.query(selectRequests, new Object[]{FriendshipStatus.REQUEST.toString(),
                id}, new PersonMapper());
        log.debug("getFriendsRequest(): personList = {}", personList);
        log.info("getFriendsRequest(): finish():");
        return personList;
    }

    public String getFriendStatus(int srcId, int dstId) {

        log.info("getFriendStatus(): start():");
        log.debug("getFriendStatus(): srcId = {}, dstId = {}", srcId, dstId);
        String select = "SELECT code FROM friendship f JOIN friendship_status fs2 ON f.status_id = fs2.id WHERE" +
                " src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        String status = jdbcTemplate.queryForObject(select, new Object[]{srcId, dstId, dstId, srcId}, String.class);
        log.debug("getFriendStatus(): status = {}", status);
        log.info("getFriendStatus(): finish():");
        return status;
    }

    public void deleteFriendForID(int srcId, int dstId) {

        log.info("deleteFriendForID(): start():");
        log.debug("deleteFriendForID(): srcId = {}, dstId = {}", srcId, dstId);
        String selectStatusId = "SELECT status_id FROM friendship WHERE src_person_id IN (?, ?) " +
                "AND dst_person_id IN (?, ?)";
        String deleteFriendshipStatus = "DELETE from friendship_status WHERE id = ?";
        String deleteFriendship = "DELETE FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        Integer selectedId = jdbcTemplate.queryForObject(selectStatusId, new Object[]{srcId, dstId, srcId, dstId},
                Integer.class);
        jdbcTemplate.update(deleteFriendship, srcId, dstId, dstId, srcId);
        jdbcTemplate.update(deleteFriendshipStatus, selectedId);
        log.info("deleteFriendForID(): finish():");
    }

    public Person getAuthPerson() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getByEmail(authentication.getName());
    }

    public void unAcceptRequest(int dstId, int srcId) {

        log.info("unAcceptRequest(): start():");
        log.debug("unAcceptRequest(): srcId = {}, dstId = {}", srcId, dstId);
        String updateFriendshipStatus = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id " +
                "FROM friendship WHERE dst_person_id = ? AND src_person_id = ?)";
        jdbcTemplate.update(updateFriendshipStatus, FriendshipStatus.DECLINED.toString(), dstId, srcId);
        log.info("unAcceptRequest(): finish():");
    }

    @Transactional
    public void updateDeclined(int srcId, int dstId) {

        log.info("updateDeclined(): start():");
        log.debug("updateDeclined(): srcId = {}, dstId = {}", srcId, dstId);
        String updateFriendship = "UPDATE friendship SET src_person_id = ?, dst_person_id = ? " +
                "WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        String updateFriendshipStatus = "UPDATE friendship_status SET code = ? WHERE id = (SELECT status_id " +
                "FROM friendship WHERE src_person_id = ? AND dst_person_id = ?)";
        jdbcTemplate.update(updateFriendship, srcId, dstId, srcId, dstId, dstId, srcId);
        jdbcTemplate.update(updateFriendshipStatus, FriendshipStatus.REQUEST.toString(), srcId, dstId);
        log.info("updateDeclined(): finish():");
    }

    public List<Person> getRecommendationsOnRegDate(int id) {

        log.info("getRecommendationsOnRegDate(): start():");
        log.debug("getRecommendationsOnRegDate(): id = {}", id);
        String selectRecommendations = "SELECT * FROM person WHERE reg_date > ? AND id != ?";
        long twoDays = 17280000000L;
        List<Person> personList = jdbcTemplate.query(selectRecommendations, new Object[]{System.currentTimeMillis() - twoDays, id},
                new PersonMapper());
        log.debug("getRecommendationsOnRegDate(): personList = {}", personList);
        log.info("getRecommendationsOnRegDate(): finish():");
        return personList;
    }

    public List<Person> searchPersons(String firstName, String lastName, Long ageFrom, Long ageTo, String country,
                                      String city) {

        log.info("getPersons(): start():");
        log.debug("getPersons(): firstName = {}, lastName = {}, ageFrom = {}, ageTo = {}, country = {}, city = {}",
                firstName, lastName, ageFrom, ageTo, country, city);

        String query = "SELECT * FROM person WHERE (first_name ILIKE ? OR ?::text IS NULL)" +
                "AND (last_name ILIKE ? OR ?::text IS NULL) AND (birth_date <= ? OR ?::bigint IS NULL) " +
                "AND (birth_date >= ? OR ?::bigint IS NULL) AND (country ILIKE ? OR ?::text IS NULL) " +
                "AND (city ILIKE ? OR ?::text IS NULL)";
        List<Person> personList = new ArrayList<>(jdbcTemplate.query(query, new Object[]{prepareParam(firstName), firstName,
                prepareParam(lastName), lastName, ageFrom, ageFrom, ageTo, ageTo, prepareParam(country), country,
                prepareParam(city), city}, new PersonMapper()));

        log.debug("getPersons(): personList = {}", personList);
        log.info("getPersons(): finish():");
        return personList;
    }

    private String prepareParam(String param) {
        return "%" + param + "%";
    }

    public List<Person> getPersonsByFirstNameSurname(String firstOrLastName) {

        log.info("getPersonsByFirstNameSurname(): start():");
        log.debug("getPersonsByFirstNameSurname(): firstOrLastName = {}", firstOrLastName);
        String query = "SELECT * FROM person WHERE (first_name ILIKE ?) " +
                "OR (last_name ILIKE ?)";
        List<Person> personList = jdbcTemplate.query(query, new Object[]{prepareParam(firstOrLastName), prepareParam(firstOrLastName)},
                new PersonMapper());
        log.debug("getPersonsByFirstNameSurname(): personList = {}", personList);
        log.info("getPersonsByFirstNameSurname(): finish():");
        return personList;
    }

    public void updateEmail(int id, String email) {

        log.info("updateEmail(): start():");
        log.debug("updateEmail(): id = {}, email = {}", id, email);
        String query = "UPDATE person SET e_mail = ? WHERE id = ?";
        jdbcTemplate.update(query, email, id);
        log.info("updateEmail(): finish():");
    }

    public Long getLastOnlineTime(int id) {

        log.info("getLastOnlineTime(): start():");
        log.debug("getLastOnlineTime(): id = {}", id);
        String query = "SELECT last_online_time FROM person WHERE id = ?";
        Long time = jdbcTemplate.queryForObject(query, new Object[]{id}, Long.class);
        log.debug("getLastOnlineTime(): time = {}", time);
        log.info("getLastOnlineTime(): finish():");
        return time;
    }

    public boolean isPersonBlockedByAnotherPerson(int blockingPerson, int blockedPerson) {
        String query = "SELECT count(*) FROM blocking_persons WHERE blocking_person_id = ? AND blocked_person_id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{blockingPerson, blockedPerson}, Integer.class) != 0;
    }

    public void blockPersonForId(int blockId, int personId) {
        String query = "INSERT INTO blocking_persons (blocking_person_id, blocked_person_id) VALUES (?, ?)";
        jdbcTemplate.update(query, personId, blockId);
    }

    public void unblockUser(int blockUserId, int userId) {
        String query = "DELETE FROM blocking_persons WHERE blocking_person_id = ? AND blocked_person_id = ?";
        jdbcTemplate.update(query, userId, blockUserId);
    }

    public void deleteRequest(int id, int id1) {
        String selectStatusId = "SELECT status_id FROM friendship WHERE src_person_id IN (?, ?) " +
                "AND dst_person_id IN (?, ?)";
        int statusId = jdbcTemplate.queryForObject(selectStatusId, new Object[]{id, id1, id1, id}, Integer.class);
        String queryForDeleteStatus = "DELETE FROM friendship_status WHERE id = ?";
        String deleteFriendship = "DELETE FROM friendship WHERE src_person_id IN (?, ?) AND dst_person_id IN (?, ?)";
        jdbcTemplate.update(deleteFriendship, id, id1, id1, id);
        jdbcTemplate.update(queryForDeleteStatus, statusId);
    }
}
