package application.requests;

import lombok.Data;

@Data
public class RegistrationDtoRequest {

    private String email;
    private String passwd1;
    private String passwd2;
    private String firstName;
    private String lastName;
    private String code;
}
