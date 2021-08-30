package application.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonSettingsDtoRequest {

    private String phone;
    private String about;
    private String city;
    private String country;

    @JsonProperty("photo_id")
    private String photoId;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("birth_date")
    private String birthDate;
}
