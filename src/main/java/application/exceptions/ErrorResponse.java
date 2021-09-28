package application.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String timestamp;
    private final String path;
    private final String error;

    @JsonProperty("error_description")
    private final String errorDescription;

    public ErrorResponse(String error, String errorDescription, String path) {
        this.path = path;
        this.error = error;
        this.errorDescription = errorDescription;
        timestamp = LocalDateTime.now().toString();
    }
}
