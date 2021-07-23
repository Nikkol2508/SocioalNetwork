package application.responses;

import lombok.Data;

@Data
public class GeneralResponse<T> {

    private String error;
    private long timestamp;
    private T data;
}
