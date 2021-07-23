package application.service;

import application.models.HTTPMessage;
import application.models.Person;
import application.responses.GeneralResponse;
import liquibase.pro.packaged.T;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public GeneralResponse<Person> getAuth() {

        GeneralResponse<Person> response = new GeneralResponse<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
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

        person.setCity("Москва");
        person.setCountry("Россия");
        person.setMessagesPermission("All");
        person.setLastOnlineTime(System.currentTimeMillis() - 40);
        person.isBlocked();
        person.setToken("kjhfgkfkjh");
        response.setData(person);
        return response;
    }

    public GeneralResponse<HTTPMessage> getLogout() {
        GeneralResponse<HTTPMessage> response = new GeneralResponse<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        response.setData(new HTTPMessage("ok"));
        return response;
    }
}
