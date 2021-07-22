package application.service;

import application.models.Person;
import application.responses.PersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonResponse personResponse;

    public PersonResponse getPerson(){
        personResponse.setError("");
        personResponse.setTimestamp(System.currentTimeMillis());
        personResponse.setTotal(0);
        personResponse.setOffset(0);
        personResponse.setPerPage(20);

        HashMap<String,Object> data = new HashMap<>();
        Person person = new Person();
        person.setId(1);
        person.setFirstName("Петр");
        person.setLastName("Иванов");
        Date date = new Date();
        person.setRegDate(date);
        GregorianCalendar birthCalendar = new GregorianCalendar(2000, 05, 21);
        Date birthDay = birthCalendar.getTime();
        person.setBirthDate(birthDay);
        person.setEmail("petriva@gmail.com");
        person.setPhone("88005553535");
        person.setPhoto("");
        person.setAbout("Родился в небольшой, но честной семье");
        HashMap<String, Object> city = new HashMap<>();
        city.put("id", 1);
        city.put("city", "Москва");

        data.put("id", person.getId());
        data.put("first_name", person.getFirstName());
        data.put("last_name", person.getLastName());
        data.put("reg_date",person.getRegDate());
        data.put("birth_date",person.getBirthDate());
        data.put("email",person.getEmail());
        data.put("phone",person.getPhone());
        data.put("photo",person.getPhoto());
        data.put("about",person.getAbout());
        data.put("city", city);
        personResponse.setData(data);

        HashMap<String,Object> country = new HashMap<>();
        country.put("id", 1);
        country.put("country", "Россия");
        personResponse.setCountry(country);
        personResponse.setMessagesPermission("ALL");
        personResponse.setLastOnlineTime(date);
        return personResponse;
    }
}
