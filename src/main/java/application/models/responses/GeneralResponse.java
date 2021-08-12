package application.models.responses;

import lombok.Data;

@Data
public class GeneralResponse<T> {

    private String error;
    private long timestamp;
    private T data;

    public GeneralResponse(T data) {
        error = "Error";
        timestamp = System.currentTimeMillis();
        this.data = data;
    }
}
