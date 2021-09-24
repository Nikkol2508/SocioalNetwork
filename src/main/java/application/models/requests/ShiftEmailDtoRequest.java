package application.models.requests;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class ShiftEmailDtoRequest {

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "{email.not.valid}")
    private String email;
}
