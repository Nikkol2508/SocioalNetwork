package application.requests;

import lombok.Data;

@Data
public class AuthDtoRequest {

    private String email;
    private String password;
}
