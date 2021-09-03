package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.FriendshipStatus;
import application.models.NotificationType;
import application.models.Person;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final DaoPerson daoPerson;
    private final DaoNotification daoNotification;

    public List<PersonDto> getUserFriends() {

        Person currentPerson = daoPerson.getAuthPerson();
        return getPersonDtoOnPerson(daoPerson.getFriends(currentPerson.getId()));
    }

    public List<PersonDto> getUserFriendsRequest() {

        List<Person> personList = daoPerson.getFriendsRequest(daoPerson.getAuthPerson().getId());
        return getPersonDtoOnPerson(personList);
    }

    public List<PersonDto> getUserFriendsRecommendations() {

        List<PersonDto> personDtoList = getPersonDtoOnPerson(daoPerson.getRecommendations(daoPerson
                .getAuthPerson().getId()));
        if (personDtoList.size() == 0) {
            personDtoList = getPersonDtoOnPerson(daoPerson.getRecommendationsOnRegDate(daoPerson
                    .getAuthPerson().getId()));
        }
        return personDtoList;
    }

    public List<PersonDto> getPersonDtoOnPerson(List<Person> personList) {

        List<PersonDto> personDtos = new ArrayList<>();
        for (Person person : personList) {
            PersonDto personDto = new PersonDto();
            personDto.setId(person.getId());
            personDto.setEmail(person.getEmail());
            personDto.setPhone(person.getPhone());
            personDto.setPhoto(person.getPhoto());
            personDto.setAbout(person.getAbout());
            personDto.setCity(person.getCity());
            personDto.setCountry(person.getCountry());
            personDto.setFirstName(person.getFirstName());
            personDto.setLastName(person.getLastName());
            personDto.setRegDate(person.getRegDate());
            personDto.setBirthDate(person.getBirthDate());
            personDto.setMessagesPermission(person.getMessagesPermission().toString());
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isBlocked());
            personDtos.add(personDto);
        }
        return personDtos;
    }

    public MessageResponseDto addFriendForId(int id) {

        Person currentPerson = daoPerson.getAuthPerson();
        String friendStatus;
        try {
            friendStatus = daoPerson.getFriendStatus(currentPerson.getId(), id);
        } catch (EmptyResultDataAccessException ex) {
            friendStatus = null;
        }
        if (friendStatus == null) {
            int entityId = daoPerson.addFriendByIdAndReturnEntityId(currentPerson.getId(), id);
            daoNotification.addNotification(id, currentPerson.getId(), System.currentTimeMillis(), entityId,
                    daoPerson.getById(id).getEmail(), NotificationType.FRIEND_REQUEST.toString());
        } else if (friendStatus.equals(FriendshipStatus.REQUEST.toString())) {
            daoPerson.addFriendRequest(id, currentPerson.getId());
        } else if (friendStatus.equals(FriendshipStatus.DECLINED.toString())) {
            daoPerson.updateDeclined(currentPerson.getId(), id);
        }
        return new MessageResponseDto();
    }

    public MessageResponseDto deleteFriendForId(int id) {

        Person currentPerson = daoPerson.getAuthPerson();
        String friendStatus = daoPerson.getFriendStatus(currentPerson.getId(), id);
        if (friendStatus.equals(FriendshipStatus.FRIEND.toString())) {
            daoPerson.deleteFriendForID(currentPerson.getId(), id);
        } else if (friendStatus.equals(FriendshipStatus.REQUEST.toString())) {
            daoPerson.unAcceptRequest(currentPerson.getId(), id);
        }
        return new MessageResponseDto();
    }
}
