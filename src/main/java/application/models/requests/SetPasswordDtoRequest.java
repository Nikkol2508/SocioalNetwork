package application.models.requests;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class SetPasswordDtoRequest {

    private String token;

    @Size(min = 8, message = "The password length must not be less than 8 characters")
    private String password;
}
