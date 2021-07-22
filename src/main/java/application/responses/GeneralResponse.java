package application.responses;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class GeneralResponse<T> {

    private String error;
    private long timestamp;
    private T data;
}
