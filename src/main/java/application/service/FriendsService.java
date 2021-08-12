package application.service;

import application.dao.DaoPerson;
import application.models.FriendshipStatus;
import application.models.MessageRequestDto;
import application.models.Person;
import application.models.PersonDto;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final DaoPerson daoPerson;

    public GeneralListResponse<PersonDto> getUserFriends() {
        Person currentPerson = daoPerson.getAuthPerson();
        GeneralListResponse<PersonDto> friendResponse = new GeneralListResponse<>(getPersonDtoOnPerson(daoPerson
                .getFriends(currentPerson.getId())));
        friendResponse.setTotal(0);
        friendResponse.setOffset(0);
        friendResponse.setPerPage(20);
        return friendResponse;
    }

    public GeneralListResponse<PersonDto> getUserFriendsRequest() {

        List<Person> personList = daoPerson.getFriendsRequest(daoPerson.getAuthPerson().getId());
        GeneralListResponse<PersonDto> requestResponse = new GeneralListResponse<>(getPersonDtoOnPerson(personList));
        requestResponse.setTotal(0);
        requestResponse.setOffset(0);
        requestResponse.setPerPage(20);

        return requestResponse;
    }

    public GeneralListResponse<PersonDto> getUserFriendsRecommendations() {

        GeneralListResponse<PersonDto> recommendationResponse = new GeneralListResponse<>(getPersonDtoOnPerson(daoPerson
                .getRecommendations()));
        recommendationResponse.setTotal(0);
        recommendationResponse.setOffset(0);
        recommendationResponse.setPerPage(20);

        return recommendationResponse;
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
            personDto.setMessagesPermission("ALL");
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isBlocked());
            personDtos.add(personDto);
        }
        return personDtos;
    }

    public GeneralResponse<MessageRequestDto> addFriendForId(int id) {
        Person currentPerson = daoPerson.getAuthPerson();
        if (daoPerson.getFriendStatus(currentPerson.getId(), id) == null) {
            daoPerson.addFriendForId(currentPerson.getId(), id);
        } else if (daoPerson.getFriendStatus(currentPerson.getId(), id).equals(FriendshipStatus.REQUEST.toString())) {
            daoPerson.addFriendRequest(id, currentPerson.getId());
        }
        return new GeneralResponse<>(new MessageRequestDto("ok"));
    }

    public GeneralResponse<MessageRequestDto> deleteFriendForId(int id) {
        Person currentPerson = daoPerson.getAuthPerson();
        if (daoPerson.getFriendStatus(id, currentPerson.getId()).equals(FriendshipStatus.FRIEND.toString())) {
            daoPerson.deleteFriendForID(currentPerson.getId(), id);
        }
        return new GeneralResponse<>(new MessageRequestDto("ok"));
    }
}
