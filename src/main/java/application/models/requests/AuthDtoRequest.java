package application.models.requests;

import lombok.Data;

@Data
public class AuthDtoRequest {

    private String email;
    private String password;
}
