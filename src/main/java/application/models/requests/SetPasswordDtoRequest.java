package application.models.requests;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class SetPasswordDtoRequest {

    private String token;

    @Size(min = 8, message = "{password.not.valid}")
    private String password;
}
