package application.service;

import application.dao.DaoPerson;
import application.exceptions.PasswordsNotEqualsException;
import application.models.LogoutDto;
import application.models.Person;
import application.models.PersonDto;
import application.requests.AuthDtoRequest;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DaoPerson daoPerson;

    public GeneralResponse<PersonDto> getAuth(AuthDtoRequest authDtoRequest) throws PasswordsNotEqualsException {

        Person person = daoPerson.getByEmail(authDtoRequest.getEmail());
        if (person.getPassword().equals(authDtoRequest.getPassword())) {
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
//            City city = new City(person.);
//            personDto.setCity(city.getTitle());
//            Country country = new Country(1, "Россия");
//            personDto.setCountry(country.getTitle());
            personDto.setMessagesPermission("ALL");
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isApproved());
            personDto.setToken("kjhfgkfkjh");
            return new GeneralResponse<>(personDto);
        } else {
            throw new PasswordsNotEqualsException();
        }
    }


    public GeneralResponse<LogoutDto> getLogout() {
        return new GeneralResponse<>(new LogoutDto("ok"));
    }

}
