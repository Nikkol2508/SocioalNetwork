package application.models.requests;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegistrationDtoRequest {

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email is not valid")
    private String email;

    @Size(min = 8, message = "The password length must not be less than 8 characters")
    private String passwd1;

    private String passwd2;

    @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$", message = "First name has invalid characters")
    private String firstName;

    @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$", message = "Second name has invalid characters")
    private String lastName;

    @Size(min = 4)
    private String code;
}
