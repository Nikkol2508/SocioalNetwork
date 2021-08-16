package application.models.requests;

import lombok.Data;

@Data
public class PersonSettingsDtoRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private long birthDate;
}
