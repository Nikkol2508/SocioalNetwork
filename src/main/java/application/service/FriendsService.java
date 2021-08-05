package application.service;

import application.models.City;
import application.models.Country;
import application.models.PersonDto;
import application.responses.GeneralListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    public GeneralListResponse<PersonDto> getUserFriends() {

        List<PersonDto> personDtoList = new ArrayList<>();

        PersonDto friend1 = new PersonDto();
        friend1.setId(3);
        friend1.setFirstName("Света");
        friend1.setLastName("Белкина");
        friend1.setRegDate(System.currentTimeMillis() - 577);
        friend1.setBirthDate(System.currentTimeMillis() - 1947);
        friend1.setEmail("gsdhgsh@skdjfhskdj.ru");
        friend1.setPhone("916332211");
        friend1.setPhoto("");
        friend1.setAbout("Немного обо мне");

        friend1.setCity("Москва");
        friend1.setCountry("Россия");
        friend1.setMessagesPermission("All");
        friend1.setLastOnlineTime(System.currentTimeMillis() - 40);
        friend1.setBlocked(false);
        friend1.setToken("kjhfgkfkjh");

        PersonDto friend2 = new PersonDto();

        friend2.setId(4);
        friend2.setFirstName("Егор");
        friend2.setLastName("Аничкин");
        friend2.setRegDate(System.currentTimeMillis() - 467);
        friend2.setBirthDate(System.currentTimeMillis() - 1987);
        friend2.setEmail("gsdfsh@skdjfhskdj.ru");
        friend2.setPhone("916333211");
        friend2.setPhoto("");
        friend2.setAbout("Немного обо мне");

        City city = new City(1, "Москва");
        friend2.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        friend2.setCountry(country.getTitle());
        friend2.setMessagesPermission("All");
        friend2.setLastOnlineTime(System.currentTimeMillis() - 40);
        friend2.setBlocked(false);

        personDtoList.add(friend1);
        personDtoList.add(friend2);
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

        List<PersonDto> personDtoList = new ArrayList<>();

        PersonDto personDtoForRecommendation = new PersonDto();

        personDtoForRecommendation.setId(15);
        personDtoForRecommendation.setFirstName("Gera");
        personDtoForRecommendation.setLastName("Rog");
        personDtoForRecommendation.setRegDate(System.currentTimeMillis() - 777);
        personDtoForRecommendation.setBirthDate(System.currentTimeMillis() - 1994);
        personDtoForRecommendation.setEmail("gsdfsh@sjfj.ru");
        personDtoForRecommendation.setPhone("91633322343");
        personDtoForRecommendation.setPhoto("");
        personDtoForRecommendation.setAbout("Немного обо мне");

        City city = new City(1, "Москва");
        personDtoForRecommendation.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        personDtoForRecommendation.setCountry(country.getTitle());
        personDtoForRecommendation.setMessagesPermission("All");
        personDtoForRecommendation.setLastOnlineTime(System.currentTimeMillis() - 40);
        personDtoForRecommendation.setBlocked(false);

        personDtoList.add(personDtoForRecommendation);
        GeneralListResponse<PersonDto> recommendationResponse = new GeneralListResponse<>(personDtoList);
        recommendationResponse.setTotal(0);
        recommendationResponse.setOffset(0);
        recommendationResponse.setPerPage(20);

        return recommendationResponse;
    }
}
