package application.service;

import application.models.Person;
import application.responses.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MeService {

    private final AuthResponse authResponse;

    public AuthResponse getAuth() {

        authResponse.setError("");
        authResponse.setTimestamp(System.currentTimeMillis());
        Person person = new Person();
        person.setId(2);
        person.setFirstName("Борис");
        person.setLastName("Булкин");
        person.setRegDate(System.currentTimeMillis() - 567);
        person.setBirthDate(System.currentTimeMillis() - 1997);
        person.setEmail("gsdfhgsh@skdjfhskdj.ru");
        person.setPhone("9163332211");
        person.setPhoto("");
        person.setAbout("Немного обо мне");

        HashMap<Integer, String> city = new HashMap<>();
        HashMap<Integer, String> country = new HashMap<>();
        city.put(1, "Москва");
        country.put(1, "Россия");
        person.setCity(city);
        person.setCountry(country);
        person.setMessages_permission("All");
        person.setLastOnlineTime(System.currentTimeMillis() - 40);
        person.isBlocked();
        person.setToken("kjhfgkfkjh");
        authResponse.setData(person);
        return authResponse;
    }
}
