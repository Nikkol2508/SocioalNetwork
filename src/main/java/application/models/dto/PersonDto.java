package application.models.dto;

import application.models.Person;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class PersonDto {
    private int id;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String token;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("reg_date")
    private long regDate;

    @JsonProperty("birth_date")
    private long birthDate;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    @JsonProperty("last_online_time")
    private long lastOnlineTime;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    public static PersonDto fromPerson(Person person) {
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
        personDto.setMessagesPermission(person.getMessagesPermission().toString());
        personDto.setLastOnlineTime(person.getLastOnlineTime());
        personDto.setBlocked(person.isBlocked());
        return personDto;
    }
}

