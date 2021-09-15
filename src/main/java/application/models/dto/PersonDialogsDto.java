package application.models.dto;

import application.models.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDialogsDto {

    private int id;
    private String email;
    private String photo;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("last_online_time")
    private long lastOnlineTime;

    public static PersonDialogsDto fromPerson(Person person) {
        PersonDialogsDto personDialogsDto = new PersonDialogsDto();
        personDialogsDto.setId(person.getId());
        personDialogsDto.setEmail(person.getEmail());
        personDialogsDto.setFirstName(person.getFirstName());
        personDialogsDto.setLastName(person.getLastName());
        personDialogsDto.setLastOnlineTime(person.getLastOnlineTime());
        personDialogsDto.setPhoto(person.getPhoto());
        return personDialogsDto;
    }


}
