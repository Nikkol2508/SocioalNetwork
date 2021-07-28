package application.service;

import application.dao.DaoPerson;
import application.models.Person;
import application.models.PersonDto;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DaoPerson daoPerson;

    public GeneralResponse<PersonDto> getPerson(){
        Person person = daoPerson.getByEmail("nik@yandex.ru");
            PersonDto personDto = new PersonDto();
            personDto.setId(person.getId());
            personDto.setFirstName(person.getFirstName());
            personDto.setLastName(person.getLastName());
            personDto.setRegDate(person.getRegDate());
            personDto.setBirthDate(person.getBirthDate());
            personDto.setEmail(person.getEmail());
            personDto.setPhone(person.getPhone());
            personDto.setPhoto(person.getPhoto());
            personDto.setAbout(person.getAbout());
            personDto.setCity(person.getCity());
            personDto.setCountry(person.getCountry());
            personDto.setMessagesPermission("ALL");
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isApproved());
            personDto.setToken("kjhfgkfkjh");
            return new GeneralResponse<>(personDto);
    }
}
