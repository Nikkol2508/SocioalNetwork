package application.models.requests;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegistrationDtoRequest {

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "{email.not.valid}")
    private String email;

    @Size(min = 8, message = "{password.not.valid}")
    private String passwd1;

    private String passwd2;

    @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$", message = "{first.name.not.valid}")
    private String firstName;

    @Pattern(regexp = "^[(a-zA-Zа-яёА-ЯЁ ,.'-]{2,50}$", message = "{last.name.not.valid}")
    private String lastName;

    @Pattern(regexp = "^\\d{4}$", message = "{code.not.valid}")
    private String code;
}
