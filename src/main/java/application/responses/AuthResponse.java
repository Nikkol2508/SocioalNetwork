package application.responses;

import application.models.Person;
import liquibase.pro.packaged.T;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthResponse<T> {

    private String error;
    private long timestamp;
    private T data;
}
