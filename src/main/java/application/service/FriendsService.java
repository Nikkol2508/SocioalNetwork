package application.service;

import application.dao.DaoPerson;
import application.models.City;
import application.models.Country;
import application.models.Person;
import application.models.PersonDto;
import application.models.responses.GeneralListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final DaoPerson personDtoDao;

    public GeneralListResponse<PersonDto> getUserFriends(int id) {
        List<PersonDto> personDtoList = getPersonDtoOnPerson(personDtoDao.getFriends(id));
        GeneralListResponse<PersonDto> friendResponse = new GeneralListResponse<>(personDtoList);
        friendResponse.setTotal(0);
        friendResponse.setOffset(0);
        friendResponse.setPerPage(20);
        return friendResponse;
    }

    public GeneralListResponse<PersonDto> getUserFriendsRequest() {

        List<PersonDto> personDtoList = new ArrayList<>();

        PersonDto request1 = new PersonDto();

        request1.setId(10);
        request1.setFirstName("Mark");
        request1.setLastName("Black");
        request1.setRegDate(System.currentTimeMillis() - 477);
        request1.setBirthDate(System.currentTimeMillis() - 1997);
        request1.setEmail("gsdfsh@skdjfhdj.ru");
        request1.setPhone("91633321143");
        request1.setPhoto("");
        request1.setAbout("Немного обо мне");

        request1.setCity("Москва");
        request1.setCountry("Россия");
        request1.setMessagesPermission("All");
        request1.setLastOnlineTime(System.currentTimeMillis() - 40);
        request1.setBlocked(false);

        PersonDto request2 = new PersonDto();

        request2.setId(11);
        request2.setFirstName("Jack");
        request2.setLastName("Reavs");
        request2.setRegDate(System.currentTimeMillis() - 577);
        request2.setBirthDate(System.currentTimeMillis() - 1999);
        request2.setEmail("gsdfsh@skdjfj.ru");
        request2.setPhone("9163332112343");
        request2.setPhoto("");
        request2.setAbout("Немного обо мне");

        City city = new City(1, "Москва");
        request2.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        request2.setCountry(country.getTitle());
        request2.setMessagesPermission("All");
        request2.setLastOnlineTime(System.currentTimeMillis() - 40);
        request2.setBlocked(false);

        personDtoList.add(request1);
        personDtoList.add(request2);
        GeneralListResponse<PersonDto> requestResponse = new GeneralListResponse<>(personDtoList);
        requestResponse.setTotal(0);
        requestResponse.setOffset(0);
        requestResponse.setPerPage(20);
        return requestResponse;
    }

    public GeneralListResponse<PersonDto> getUserFriendsRecommendations() {
        List<Person> personList = personDtoDao.getRecommendations();
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

        GeneralListResponse<PersonDto> recommendationResponse = new GeneralListResponse<>(personDtos);
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
}
