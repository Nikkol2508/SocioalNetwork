package application.responses;

import application.models.Person;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AuthResponse {

    private String error;
    private long timestamp;
    private Person person;
}
