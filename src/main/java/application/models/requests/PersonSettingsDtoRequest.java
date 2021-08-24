package application.models.requests;

import lombok.Data;

@Data
public class PersonSettingsDtoRequest {

    private String first_name;
    private String last_name;
    private String phone;
    private String photo_id;
    private String about;
    private String city;
    private String country;
    private String birth_date;
}
