package application.service;

import application.dao.DaoPerson;
import application.models.FriendshipStatus;
import application.models.Person;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
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
        List<PersonDto> personDtoList = getPersonDtoOnPerson(daoPerson
                .getRecommendations(daoPerson.getAuthPerson().getId()));

        if (personDtoList.size() == 0) {
            personDtoList = getPersonDtoOnPerson(daoPerson.getRecommendationsOnRegDate(daoPerson.getAuthPerson().getId()));
        }

        GeneralListResponse<PersonDto> recommendationResponse = new GeneralListResponse<>(personDtoList);
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
            personDto.setMessagesPermission(person.getMessagesPermission().toString());
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isBlocked());
            personDtos.add(personDto);
        }
        return personDtos;
    }

    public GeneralResponse<MessageResponseDto> addFriendForId(int id) {
        Person currentPerson = daoPerson.getAuthPerson();
        String friendStatus = daoPerson.getFriendStatus(currentPerson.getId(), id);
        if (friendStatus == null) {
            daoPerson.addFriendForId(currentPerson.getId(), id);
        } else if (friendStatus.equals(FriendshipStatus.REQUEST.toString())) {
            daoPerson.addFriendRequest(id, currentPerson.getId());
        } else if (friendStatus.equals(FriendshipStatus.DECLINED.toString())) {
            daoPerson.updateDeclined(currentPerson.getId(), id);
        }
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }

    public GeneralResponse<MessageResponseDto> deleteFriendForId(int id) {
        Person currentPerson = daoPerson.getAuthPerson();
        String friendStatus = daoPerson.getFriendStatus(currentPerson.getId(), id);
        if (friendStatus.equals(FriendshipStatus.FRIEND.toString())) {
            daoPerson.deleteFriendForID(currentPerson.getId(), id);
        } else if (friendStatus.equals(FriendshipStatus.REQUEST.toString())) {
            daoPerson.unAcceptRequest(currentPerson.getId(), id);
        }
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }
}
