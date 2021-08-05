package application.requests;

import lombok.Data;

@Data
public class SetPasswordDtoRequest {

    private String token;
    private String password;
}
