package application.models.requests;

import lombok.Data;

@Data
public class SetPasswordDtoRequest {

    private String token;
    private String password;
}
