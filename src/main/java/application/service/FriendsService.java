package application.service;

import application.models.City;
import application.models.Country;
import application.models.Person;
import application.responses.GeneralListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    public GeneralListResponse<Person> getUserFriends() {
        GeneralListResponse<Person> friendResponse = new GeneralListResponse<>();
        friendResponse.setError("");
        friendResponse.setTimestamp(System.currentTimeMillis());
        friendResponse.setTotal(0);
        friendResponse.setOffset(0);
        friendResponse.setPerPage(20);

        List<Person> personList = new ArrayList<>();

        Person friend1 = new Person();
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

        Person friend2 = new Person();

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

        personList.add(friend1);
        personList.add(friend2);
        friendResponse.setData(personList);
        return friendResponse;
    }

    public GeneralListResponse<Person> getUserFriendsRequest() {
        GeneralListResponse<Person> requestResponse = new GeneralListResponse<>();
        requestResponse.setError("");
        requestResponse.setTimestamp(System.currentTimeMillis());
        requestResponse.setTotal(0);
        requestResponse.setOffset(0);
        requestResponse.setPerPage(20);

        List<Person> personList = new ArrayList<>();

        Person request1 = new Person();

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

        Person request2 = new Person();

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

        personList.add(request1);
        personList.add(request2);
        requestResponse.setData(personList);
        return requestResponse;
    }

    public GeneralListResponse<Person> getUserFriendsRecommendations() {
        GeneralListResponse<Person> recommendationResponse = new GeneralListResponse<>();
        recommendationResponse.setError("");
        recommendationResponse.setTimestamp(System.currentTimeMillis());
        recommendationResponse.setTotal(0);
        recommendationResponse.setOffset(0);
        recommendationResponse.setPerPage(20);

        List<Person> personList = new ArrayList<>();

        Person personForRecommendation = new Person();

        personForRecommendation.setId(15);
        personForRecommendation.setFirstName("Gera");
        personForRecommendation.setLastName("Rog");
        personForRecommendation.setRegDate(System.currentTimeMillis() - 777);
        personForRecommendation.setBirthDate(System.currentTimeMillis() - 1994);
        personForRecommendation.setEmail("gsdfsh@sjfj.ru");
        personForRecommendation.setPhone("91633322343");
        personForRecommendation.setPhoto("");
        personForRecommendation.setAbout("Немного обо мне");

        City city = new City(1, "Москва");
        personForRecommendation.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        personForRecommendation.setCountry(country.getTitle());
        personForRecommendation.setMessagesPermission("All");
        personForRecommendation.setLastOnlineTime(System.currentTimeMillis() - 40);
        personForRecommendation.setBlocked(false);

        personList.add(personForRecommendation);
        recommendationResponse.setData(personList);

        return recommendationResponse;
    }
}
